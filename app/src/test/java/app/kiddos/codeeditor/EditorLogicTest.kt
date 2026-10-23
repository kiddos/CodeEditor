package app.kiddos.codeeditor

import app.kiddos.codeeditor.data.EditorTheme
import app.kiddos.codeeditor.data.EncodingHelper
import app.kiddos.codeeditor.data.ProgrammingLanguage
import app.kiddos.codeeditor.search.SearchState
import app.kiddos.codeeditor.syntax.SyntaxHighlighter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets

class EditorLogicTest {

    @Test
    fun testLanguageDetection() {
        assertEquals(ProgrammingLanguage.CPP, ProgrammingLanguage.fromFileName("main.cpp"))
        assertEquals(ProgrammingLanguage.CPP, ProgrammingLanguage.fromFileName("header.hpp"))
        assertEquals(ProgrammingLanguage.PYTHON, ProgrammingLanguage.fromFileName("test.py"))
        assertEquals(ProgrammingLanguage.RUST, ProgrammingLanguage.fromFileName("lib.rs"))
        assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromFileName("App.java"))
        assertEquals(ProgrammingLanguage.KOTLIN, ProgrammingLanguage.fromFileName("Main.kt"))
        assertEquals(ProgrammingLanguage.PLAIN_TEXT, ProgrammingLanguage.fromFileName("notes.txt"))
        assertEquals(ProgrammingLanguage.PLAIN_TEXT, ProgrammingLanguage.fromFileName("unknown"))
    }

    @Test
    fun testEncodingConversion() {
        val originalText = "Hello 世界! Rust & C++ & Python & Java"
        val utf8Bytes = EncodingHelper.encodeString(originalText, StandardCharsets.UTF_8)
        val decodedUtf8 = EncodingHelper.decodeBytes(utf8Bytes, StandardCharsets.UTF_8)
        assertEquals(originalText, decodedUtf8)

        val utf16LeBytes = EncodingHelper.encodeString(originalText, StandardCharsets.UTF_16LE)
        val decodedUtf16Le = EncodingHelper.decodeBytes(utf16LeBytes, StandardCharsets.UTF_16LE)
        assertEquals(originalText, decodedUtf16Le)

        // UTF-8 BOM detection
        val withBom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) + utf8Bytes
        val detected = EncodingHelper.detectCharset(withBom)
        assertEquals(StandardCharsets.UTF_8, detected)
        val decodedWithBom = EncodingHelper.decodeBytes(withBom, StandardCharsets.UTF_8)
        assertEquals(originalText, decodedWithBom)
    }

    @Test
    fun testSearchNavigation() {
        val text = "val foo = 1\nval bar = 2\nval foo = 3\nval baz = foo"
        val state = SearchState(searchQuery = "foo").findMatches(text)

        assertEquals(3, state.totalMatches)
        assertEquals(0, state.currentMatchIndex)

        val next1 = state.nextMatch()
        assertEquals(1, next1.currentMatchIndex)

        val next2 = next1.nextMatch()
        assertEquals(2, next2.currentMatchIndex)

        val wrapToFirst = next2.nextMatch()
        assertEquals(0, wrapToFirst.currentMatchIndex)

        val prev = wrapToFirst.previousMatch()
        assertEquals(2, prev.currentMatchIndex)
    }

    @Test
    fun testSearchCaseSensitiveAndRegex() {
        val text = "Apple apple APPLE banana"

        // Case-insensitive
        val ciState = SearchState(searchQuery = "apple", caseSensitive = false).findMatches(text)
        assertEquals(3, ciState.totalMatches)

        // Case-sensitive
        val csState = SearchState(searchQuery = "apple", caseSensitive = true).findMatches(text)
        assertEquals(1, csState.totalMatches)

        // Regex
        val regexState = SearchState(searchQuery = "a[a-z]+e", isRegex = true, caseSensitive = true).findMatches(text)
        assertEquals(1, regexState.totalMatches) // matches only lowercase "apple"
    }

    @Test
    fun testReplaceCurrentAndReplaceAll() {
        val text = "cat and cat and cat"
        val state = SearchState(searchQuery = "cat", replaceQuery = "dog").findMatches(text)
        assertEquals(3, state.totalMatches)

        // Replace current match
        val replaceCurrentResult = state.replaceCurrent(text)
        assertNotNull(replaceCurrentResult)
        assertEquals("dog and cat and cat", replaceCurrentResult!!.first)

        // Replace all matches
        val replaceAllResult = state.replaceAll(text)
        assertEquals("dog and dog and dog", replaceAllResult.first)
        assertEquals(0, replaceAllResult.second.totalMatches)
    }

    @Test
    fun testSyntaxHighlighterProducesAnnotatedString() {
        val cppCode = "#include <iostream>\nint main() {\n    // comment\n    std::cout << \"Hello\" << 42;\n    return 0;\n}"
        val highlighted = SyntaxHighlighter.highlight(
            text = cppCode,
            language = ProgrammingLanguage.CPP,
            theme = EditorTheme.OneDark
        )

        assertEquals(cppCode, highlighted.text)
        assertTrue("Highlighted text should contain span styles", highlighted.spanStyles.isNotEmpty())
    }

    @Test
    fun testThemeCollection() {
        assertTrue(EditorTheme.allThemes.size >= 5)
        val themeIds = EditorTheme.allThemes.map { it.id }
        assertTrue(themeIds.contains("one_dark"))
        assertTrue(themeIds.contains("monokai_pro"))
        assertTrue(themeIds.contains("dracula"))
        assertTrue(themeIds.contains("nord"))
        assertTrue(themeIds.contains("solarized_dark"))
        assertTrue(themeIds.contains("github_light"))
        assertTrue(themeIds.contains("solarized_light"))
    }

    @Test
    fun testInitialStateHasNoDefaultCode() {
        val viewModel = app.kiddos.codeeditor.ui.EditorViewModel()
        val state = viewModel.uiState.value
        assertEquals(1, state.tabs.size)
        assertEquals("", state.tabs[0].content)
        assertEquals("untitled.txt", state.tabs[0].title)
    }
}
