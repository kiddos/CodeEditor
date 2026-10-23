package app.kiddos.codeeditor.syntax

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import app.kiddos.codeeditor.data.EditorTheme
import app.kiddos.codeeditor.data.ProgrammingLanguage
import java.util.regex.Pattern

object SyntaxHighlighter {

    private val numberPattern = Pattern.compile(
        "\\b(0x[0-9a-fA-F_]+|0b[01_]+|0o[0-7_]+|\\d+(_\\d+)*(\\.\\d+(_\\d+)*)?([eE][+-]?\\d+)?([fFdDlL]|u|ul|ull|i32|u32|i64|u64|f32|f64)?)\\b"
    )

    private val cppPreprocessorPattern = Pattern.compile(
        "#[ \\t]*(include|define|undef|ifdef|ifndef|if|elif|else|endif|pragma|line|error)[^\\n]*"
    )

    private val annotationPattern = Pattern.compile(
        "@[a-zA-Z_]\\w*"
    )

    private val rustMacroPattern = Pattern.compile(
        "\\b[a-zA-Z_]\\w*!"
    )

    private val wordPattern = Pattern.compile(
        "\\b[a-zA-Z_]\\w*\\b"
    )

    private val operatorPattern = Pattern.compile(
        "([+\\-*/%&|^~<>=!?:;]+|::|->|=>)"
    )

    fun highlight(
        text: String,
        language: ProgrammingLanguage,
        theme: EditorTheme,
        searchMatches: List<IntRange> = emptyList(),
        currentSearchIndex: Int = -1
    ): AnnotatedString {
        if (text.isEmpty()) return AnnotatedString("")

        val builder = AnnotatedString.Builder(text)
        val n = text.length

        // Array tracking which characters have already been styled by higher precedence tokens (e.g. comments, strings)
        val styled = BooleanArray(n)

        fun markAndStyle(start: Int, end: Int, style: SpanStyle) {
            val safeStart = start.coerceIn(0, n)
            val safeEnd = end.coerceIn(0, n)
            if (safeStart < safeEnd) {
                builder.addStyle(style, safeStart, safeEnd)
                for (i in safeStart until safeEnd) {
                    styled[i] = true
                }
            }
        }

        // 1. Comments and Strings (Highest precedence)
        when (language) {
            ProgrammingLanguage.PYTHON -> {
                // Triple-quoted docstrings / multi-line strings
                val tripleQuotePattern = Pattern.compile("(\"\"\"[\\s\\S]*?(\"\"\"|$))|('''[\\s\\S]*?('''|$))")
                val tqMatcher = tripleQuotePattern.matcher(text)
                while (tqMatcher.find()) {
                    markAndStyle(tqMatcher.start(), tqMatcher.end(), SpanStyle(color = theme.stringColor))
                }

                // Single line comments
                val commentMatcher = Pattern.compile("#[^\\n]*").matcher(text)
                while (commentMatcher.find()) {
                    if (!styled[commentMatcher.start()]) {
                        markAndStyle(commentMatcher.start(), commentMatcher.end(), SpanStyle(color = theme.commentColor))
                    }
                }
            }
            ProgrammingLanguage.SQL -> {
                val blockComment = Pattern.compile("/\\*[\\s\\S]*?(\\*/|$)").matcher(text)
                while (blockComment.find()) {
                    markAndStyle(blockComment.start(), blockComment.end(), SpanStyle(color = theme.commentColor))
                }
                val lineComment = Pattern.compile("--[^\\n]*").matcher(text)
                while (lineComment.find()) {
                    if (!styled[lineComment.start()]) {
                        markAndStyle(lineComment.start(), lineComment.end(), SpanStyle(color = theme.commentColor))
                    }
                }
            }
            ProgrammingLanguage.HTML -> {
                val htmlComment = Pattern.compile("<!--[\\s\\S]*?(-->|$)").matcher(text)
                while (htmlComment.find()) {
                    markAndStyle(htmlComment.start(), htmlComment.end(), SpanStyle(color = theme.commentColor))
                }
            }
            else -> {
                // C++, Rust, Java, Kotlin, JS, TS, etc.
                val blockComment = Pattern.compile("/\\*[\\s\\S]*?(\\*/|$)").matcher(text)
                while (blockComment.find()) {
                    markAndStyle(blockComment.start(), blockComment.end(), SpanStyle(color = theme.commentColor))
                }
                val lineComment = Pattern.compile("//[^\\n]*").matcher(text)
                while (lineComment.find()) {
                    if (!styled[lineComment.start()]) {
                        markAndStyle(lineComment.start(), lineComment.end(), SpanStyle(color = theme.commentColor))
                    }
                }
            }
        }

        // 2. Regular Strings
        val stringPattern = Pattern.compile("(\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])*'|`(\\\\.|[^`\\\\])*`)")
        val strMatcher = stringPattern.matcher(text)
        while (strMatcher.find()) {
            if (!styled[strMatcher.start()]) {
                markAndStyle(strMatcher.start(), strMatcher.end(), SpanStyle(color = theme.stringColor))
            }
        }

        // 3. Preprocessor / Annotations / Macros
        if (language == ProgrammingLanguage.CPP) {
            val prepMatcher = cppPreprocessorPattern.matcher(text)
            while (prepMatcher.find()) {
                if (!styled[prepMatcher.start()]) {
                    markAndStyle(prepMatcher.start(), prepMatcher.end(), SpanStyle(color = theme.preprocessorColor))
                }
            }
        } else if (language == ProgrammingLanguage.RUST) {
            val macroMatcher = rustMacroPattern.matcher(text)
            while (macroMatcher.find()) {
                if (!styled[macroMatcher.start()]) {
                    markAndStyle(macroMatcher.start(), macroMatcher.end(), SpanStyle(color = theme.preprocessorColor, fontWeight = FontWeight.Bold))
                }
            }
        } else if (language == ProgrammingLanguage.JAVA || language == ProgrammingLanguage.KOTLIN || language == ProgrammingLanguage.PYTHON) {
            val annMatcher = annotationPattern.matcher(text)
            while (annMatcher.find()) {
                if (!styled[annMatcher.start()]) {
                    markAndStyle(annMatcher.start(), annMatcher.end(), SpanStyle(color = theme.preprocessorColor))
                }
            }
        }

        // 4. Numbers
        val numMatcher = numberPattern.matcher(text)
        while (numMatcher.find()) {
            if (!styled[numMatcher.start()]) {
                markAndStyle(numMatcher.start(), numMatcher.end(), SpanStyle(color = theme.numberColor))
            }
        }

        // 5. Keywords and Types
        val keywords = LanguageRules.getKeywords(language)
        val types = LanguageRules.getTypes(language)

        val wMatcher = wordPattern.matcher(text)
        while (wMatcher.find()) {
            val start = wMatcher.start()
            val end = wMatcher.end()
            if (!styled[start]) {
                val word = wMatcher.group()
                when {
                    keywords.contains(word) -> {
                        markAndStyle(start, end, SpanStyle(color = theme.keywordColor, fontWeight = FontWeight.Bold))
                    }
                    types.contains(word) -> {
                        markAndStyle(start, end, SpanStyle(color = theme.typeColor))
                    }
                    // Function calls: word followed by optional space then '('
                    end < n && isFunctionCall(text, end) -> {
                        markAndStyle(start, end, SpanStyle(color = theme.functionColor))
                    }
                }
            }
        }

        // 6. Operators
        val opMatcher = operatorPattern.matcher(text)
        while (opMatcher.find()) {
            if (!styled[opMatcher.start()]) {
                markAndStyle(opMatcher.start(), opMatcher.end(), SpanStyle(color = theme.operatorColor))
            }
        }

        // 7. Search Match Highlights (Overlay)
        searchMatches.forEachIndexed { index, range ->
            val safeStart = range.first.coerceIn(0, n)
            val safeEnd = (range.last + 1).coerceIn(0, n)
            if (safeStart < safeEnd) {
                if (index == currentSearchIndex) {
                    builder.addStyle(
                        SpanStyle(
                            background = theme.currentSearchMatchBackground,
                            fontWeight = FontWeight.Bold
                        ),
                        safeStart,
                        safeEnd
                    )
                } else {
                    builder.addStyle(
                        SpanStyle(
                            background = theme.searchMatchBackground
                        ),
                        safeStart,
                        safeEnd
                    )
                }
            }
        }

        return builder.toAnnotatedString()
    }

    private fun isFunctionCall(text: String, afterWordIndex: Int): Boolean {
        var idx = afterWordIndex
        while (idx < text.length && (text[idx] == ' ' || text[idx] == '\t')) {
            idx++
        }
        return idx < text.length && text[idx] == '('
    }
}

class CodeVisualTransformation(
    private val language: ProgrammingLanguage,
    private val theme: EditorTheme,
    private val searchMatches: List<IntRange> = emptyList(),
    private val currentSearchIndex: Int = -1
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val highlighted = SyntaxHighlighter.highlight(
            text = text.text,
            language = language,
            theme = theme,
            searchMatches = searchMatches,
            currentSearchIndex = currentSearchIndex
        )
        return TransformedText(highlighted, OffsetMapping.Identity)
    }
}
