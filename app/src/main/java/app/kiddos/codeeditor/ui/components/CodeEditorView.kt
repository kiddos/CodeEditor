package app.kiddos.codeeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kiddos.codeeditor.data.EditorTheme
import app.kiddos.codeeditor.data.ProgrammingLanguage
import app.kiddos.codeeditor.search.SearchState
import app.kiddos.codeeditor.syntax.CodeVisualTransformation

import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CodeEditorView(
    content: String,
    language: ProgrammingLanguage,
    theme: EditorTheme,
    searchState: SearchState,
    isWordWrapEnabled: Boolean,
    fontSizeSp: Float,
    onContentChanged: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(content) {
        mutableStateOf(TextFieldValue(text = content, selection = TextRange(0)))
    }

    // When search match changes, move cursor to the match
    LaunchedEffect(searchState.currentMatchIndex, searchState.matches) {
        if (searchState.currentMatchIndex in searchState.matches.indices) {
            val range = searchState.matches[searchState.currentMatchIndex]
            textFieldValue = textFieldValue.copy(
                selection = TextRange(range.first, range.last + 1)
            )
        }
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val lines = remember(content) {
        val count = content.count { it == '\n' } + 1
        count
    }

    val currentLineIndex = remember(textFieldValue.selection.start, textFieldValue.text) {
        val safePos = textFieldValue.selection.start.coerceIn(0, textFieldValue.text.length)
        textFieldValue.text.substring(0, safePos).count { it == '\n' }
    }

    val lineNumbersAnnotated = remember(lines, currentLineIndex, theme) {
        buildAnnotatedString {
            for (i in 1..lines) {
                val isCurrent = (i - 1) == currentLineIndex
                val str = "$i"
                val start = length
                append(str)
                if (isCurrent) {
                    addStyle(
                        SpanStyle(
                            color = theme.keywordColor,
                            fontWeight = FontWeight.Bold
                        ),
                        start,
                        length
                    )
                } else {
                    addStyle(
                        SpanStyle(
                            color = theme.gutterText
                        ),
                        start,
                        length
                    )
                }
                if (i < lines) append("\n")
            }
        }
    }

    val lineHeight = (fontSizeSp * 1.5).sp
    val monoStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = fontSizeSp.sp,
        lineHeight = lineHeight,
        color = theme.foreground
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .verticalScroll(verticalScrollState)
    ) {
        // Line Numbers Gutter
        Box(
            modifier = Modifier
                .background(theme.gutterBackground)
                .drawBehind {
                    drawLine(
                        color = theme.currentLineBackground,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = lineNumbersAnnotated,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = fontSizeSp.sp,
                    lineHeight = lineHeight,
                    textAlign = TextAlign.End
                ),
                modifier = Modifier.widthIn(min = 36.dp)
            )
        }

        // Editor Area
        val editorModifier = if (isWordWrapEnabled) {
            Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        } else {
            Modifier
                .weight(1f)
                .horizontalScroll(horizontalScrollState)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        }

        val visualTransformation = remember(language, theme, searchState.matches, searchState.currentMatchIndex) {
            CodeVisualTransformation(
                language = language,
                theme = theme,
                searchMatches = searchState.matches,
                currentSearchIndex = searchState.currentMatchIndex
            )
        }

        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onContentChanged(newValue.text, newValue.selection.start)
            },
            textStyle = monoStyle,
            cursorBrush = SolidColor(theme.keywordColor),
            visualTransformation = visualTransformation,
            modifier = editorModifier
        )
    }
}
