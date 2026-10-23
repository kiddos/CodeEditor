package app.kiddos.codeeditor.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import app.kiddos.codeeditor.data.EditorTab
import app.kiddos.codeeditor.data.EditorTheme
import app.kiddos.codeeditor.data.EncodingHelper
import app.kiddos.codeeditor.data.ProgrammingLanguage
import app.kiddos.codeeditor.data.SampleCodes
import app.kiddos.codeeditor.search.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

data class EditorUiState(
    val tabs: List<EditorTab> = emptyList(),
    val activeTabIndex: Int = 0,
    val theme: EditorTheme = EditorTheme.OneDark,
    val searchState: SearchState = SearchState(),
    val isWordWrapEnabled: Boolean = false,
    val fontSizeSp: Float = 14f,
    val messageSnackbar: String? = null
) {
    val activeTab: EditorTab?
        get() = if (activeTabIndex in tabs.indices) tabs[activeTabIndex] else tabs.firstOrNull()
}

class EditorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private fun createInitialState(): EditorUiState {
        val defaultTab = EditorTab(
            title = "untitled.txt",
            content = "",
            language = ProgrammingLanguage.PLAIN_TEXT,
            encoding = StandardCharsets.UTF_8
        )

        return EditorUiState(
            tabs = listOf(defaultTab),
            activeTabIndex = 0,
            theme = EditorTheme.OneDark
        )
    }

    fun selectTab(index: Int) {
        _uiState.update { state ->
            if (index in state.tabs.indices) {
                val updatedSearch = state.searchState.findMatches(state.tabs[index].content)
                state.copy(activeTabIndex = index, searchState = updatedSearch)
            } else {
                state
            }
        }
    }

    fun addNewTab(
        title: String = "Untitled-${_uiState.value.tabs.size + 1}.txt",
        content: String = "",
        language: ProgrammingLanguage = ProgrammingLanguage.PLAIN_TEXT
    ) {
        val newTab = EditorTab(
            title = title,
            content = content,
            language = language,
            encoding = StandardCharsets.UTF_8
        )
        _uiState.update { state ->
            val updatedTabs = state.tabs + newTab
            val newIndex = updatedTabs.lastIndex
            state.copy(
                tabs = updatedTabs,
                activeTabIndex = newIndex,
                searchState = state.searchState.findMatches(content)
            )
        }
    }

    fun addSampleTab(language: ProgrammingLanguage) {
        val (title, content) = when (language) {
            ProgrammingLanguage.CPP -> "main.cpp" to SampleCodes.cppSample
            ProgrammingLanguage.PYTHON -> "script.py" to SampleCodes.pythonSample
            ProgrammingLanguage.RUST -> "main.rs" to SampleCodes.rustSample
            ProgrammingLanguage.JAVA -> "Main.java" to SampleCodes.javaSample
            else -> "sample.txt" to "Welcome to Code Editor!"
        }
        addNewTab(title = title, content = content, language = language)
    }

    fun closeTab(index: Int) {
        _uiState.update { state ->
            if (index !in state.tabs.indices) return@update state

            val newTabs = state.tabs.toMutableList()
            newTabs.removeAt(index)

            if (newTabs.isEmpty()) {
                // If all closed, create one clean untitled tab
                val defaultTab = EditorTab(
                    title = "Untitled-1.txt",
                    content = "",
                    language = ProgrammingLanguage.PLAIN_TEXT,
                    encoding = StandardCharsets.UTF_8
                )
                state.copy(
                    tabs = listOf(defaultTab),
                    activeTabIndex = 0,
                    searchState = state.searchState.findMatches("")
                )
            } else {
                val newActiveIndex = if (index <= state.activeTabIndex) {
                    (state.activeTabIndex - 1).coerceAtLeast(0)
                } else {
                    state.activeTabIndex.coerceIn(0, newTabs.lastIndex)
                }
                state.copy(
                    tabs = newTabs,
                    activeTabIndex = newActiveIndex,
                    searchState = state.searchState.findMatches(newTabs[newActiveIndex].content)
                )
            }
        }
    }

    fun updateContent(newContent: String, cursorPosition: Int = 0) {
        _uiState.update { state ->
            val activeTab = state.activeTab ?: return@update state
            if (activeTab.content == newContent && activeTab.cursorPosition == cursorPosition) return@update state

            val newUndo = if (activeTab.content != newContent) {
                (activeTab.undoStack + activeTab.content).takeLast(50)
            } else {
                activeTab.undoStack
            }

            val updatedTab = activeTab.copy(
                content = newContent,
                cursorPosition = cursorPosition,
                isModified = true,
                undoStack = newUndo,
                redoStack = if (activeTab.content != newContent) emptyList() else activeTab.redoStack
            )

            val updatedTabs = state.tabs.toMutableList()
            updatedTabs[state.activeTabIndex] = updatedTab

            val updatedSearch = if (state.searchState.searchQuery.isNotEmpty()) {
                state.searchState.findMatches(newContent)
            } else {
                state.searchState
            }

            state.copy(tabs = updatedTabs, searchState = updatedSearch)
        }
    }

    fun undo() {
        _uiState.update { state ->
            val activeTab = state.activeTab ?: return@update state
            if (activeTab.undoStack.isEmpty()) return@update state

            val previousContent = activeTab.undoStack.last()
            val newUndo = activeTab.undoStack.dropLast(1)
            val newRedo = (activeTab.redoStack + activeTab.content).takeLast(50)

            val updatedTab = activeTab.copy(
                content = previousContent,
                undoStack = newUndo,
                redoStack = newRedo,
                isModified = true
            )

            val updatedTabs = state.tabs.toMutableList()
            updatedTabs[state.activeTabIndex] = updatedTab
            val updatedSearch = state.searchState.findMatches(previousContent)

            state.copy(tabs = updatedTabs, searchState = updatedSearch)
        }
    }

    fun redo() {
        _uiState.update { state ->
            val activeTab = state.activeTab ?: return@update state
            if (activeTab.redoStack.isEmpty()) return@update state

            val nextContent = activeTab.redoStack.last()
            val newRedo = activeTab.redoStack.dropLast(1)
            val newUndo = (activeTab.undoStack + activeTab.content).takeLast(50)

            val updatedTab = activeTab.copy(
                content = nextContent,
                undoStack = newUndo,
                redoStack = newRedo,
                isModified = true
            )

            val updatedTabs = state.tabs.toMutableList()
            updatedTabs[state.activeTabIndex] = updatedTab
            val updatedSearch = state.searchState.findMatches(nextContent)

            state.copy(tabs = updatedTabs, searchState = updatedSearch)
        }
    }

    fun insertTextAtCursor(textToInsert: String) {
        val active = _uiState.value.activeTab ?: return
        val pos = active.cursorPosition.coerceIn(0, active.content.length)
        val before = active.content.substring(0, pos)
        val after = active.content.substring(pos)
        val newContent = before + textToInsert + after
        val newCursor = pos + textToInsert.length
        updateContent(newContent, newCursor)
    }

    fun indent() {
        insertTextAtCursor("    ")
    }

    fun setTheme(theme: EditorTheme) {
        _uiState.update { it.copy(theme = theme) }
    }

    fun setLanguage(language: ProgrammingLanguage) {
        _uiState.update { state ->
            val activeTab = state.activeTab ?: return@update state
            val updatedTab = activeTab.copy(language = language)
            val updatedTabs = state.tabs.toMutableList()
            updatedTabs[state.activeTabIndex] = updatedTab
            state.copy(tabs = updatedTabs)
        }
    }

    fun setEncoding(charset: Charset) {
        _uiState.update { state ->
            val activeTab = state.activeTab ?: return@update state
            val updatedTab = activeTab.copy(encoding = charset, isModified = true)
            val updatedTabs = state.tabs.toMutableList()
            updatedTabs[state.activeTabIndex] = updatedTab
            state.copy(tabs = updatedTabs, messageSnackbar = "Encoding set to ${charset.displayName()}")
        }
    }

    fun reloadWithEncoding(charset: Charset, context: Context) {
        val activeTab = _uiState.value.activeTab ?: return
        val rawBytes = activeTab.rawBytes ?: if (activeTab.uri != null) {
            try {
                context.contentResolver.openInputStream(activeTab.uri)?.use { it.readBytes() }
            } catch (_: Exception) {
                null
            }
        } else {
            // Unsaved file: re-encode from previous encoding and decode with new
            EncodingHelper.encodeString(activeTab.content, activeTab.encoding)
        }

        if (rawBytes != null) {
            val reloadedContent = EncodingHelper.decodeBytes(rawBytes, charset)
            _uiState.update { state ->
                val updatedTab = activeTab.copy(
                    content = reloadedContent,
                    encoding = charset,
                    rawBytes = rawBytes,
                    isModified = false
                )
                val updatedTabs = state.tabs.toMutableList()
                updatedTabs[state.activeTabIndex] = updatedTab
                val updatedSearch = state.searchState.findMatches(reloadedContent)
                state.copy(
                    tabs = updatedTabs,
                    searchState = updatedSearch,
                    messageSnackbar = "Reloaded with ${charset.displayName()}"
                )
            }
        }
    }

    fun openFile(context: Context, uri: Uri) {
        try {
            val contentResolver = context.contentResolver
            val fileName = queryFileName(context, uri) ?: "File"
            val rawBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
            val detectedCharset = EncodingHelper.detectCharset(rawBytes)
            val text = EncodingHelper.decodeBytes(rawBytes, detectedCharset)
            val language = ProgrammingLanguage.fromFileName(fileName)

            val newTab = EditorTab(
                title = fileName,
                uri = uri,
                content = text,
                language = language,
                encoding = detectedCharset,
                isModified = false,
                rawBytes = rawBytes
            )

            _uiState.update { state ->
                // Check if already open
                val existingIndex = state.tabs.indexOfFirst { it.uri == uri }
                if (existingIndex >= 0) {
                    state.copy(activeTabIndex = existingIndex)
                } else {
                    val updatedTabs = state.tabs + newTab
                    state.copy(
                        tabs = updatedTabs,
                        activeTabIndex = updatedTabs.lastIndex,
                        searchState = state.searchState.findMatches(text),
                        messageSnackbar = "Loaded $fileName (${detectedCharset.displayName()})"
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(messageSnackbar = "Failed to open file: ${e.message}") }
        }
    }

    fun saveCurrentFile(context: Context, saveAsUri: Uri? = null) {
        val activeTab = _uiState.value.activeTab ?: return
        val targetUri = saveAsUri ?: activeTab.uri
        if (targetUri == null) {
            _uiState.update { it.copy(messageSnackbar = "Please choose a save location") }
            return
        }

        try {
            val bytes = EncodingHelper.encodeString(activeTab.content, activeTab.encoding)
            context.contentResolver.openOutputStream(targetUri, "wt")?.use { output ->
                output.write(bytes)
                output.flush()
            }

            val savedName = if (saveAsUri != null) queryFileName(context, saveAsUri) ?: activeTab.title else activeTab.title
            val language = ProgrammingLanguage.fromFileName(savedName)

            _uiState.update { state ->
                val updatedTab = activeTab.copy(
                    title = savedName,
                    uri = targetUri,
                    language = language,
                    isModified = false,
                    rawBytes = bytes
                )
                val updatedTabs = state.tabs.toMutableList()
                updatedTabs[state.activeTabIndex] = updatedTab
                state.copy(
                    tabs = updatedTabs,
                    messageSnackbar = "Saved $savedName (${activeTab.encoding.displayName()})"
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(messageSnackbar = "Failed to save: ${e.message}") }
        }
    }

    private fun queryFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        return cursor.getString(nameIndex)
                    }
                }
            } catch (_: Exception) {
            }
        }
        return uri.lastPathSegment
    }

    // --- Search & Replace Actions ---

    fun toggleSearch(open: Boolean? = null) {
        _uiState.update { state ->
            val shouldOpen = open ?: !state.searchState.isSearchOpen
            val newSearch = if (shouldOpen) {
                state.searchState.copy(isSearchOpen = true).findMatches(state.activeTab?.content ?: "")
            } else {
                state.searchState.copy(isSearchOpen = false, isReplaceOpen = false)
            }
            state.copy(searchState = newSearch)
        }
    }

    fun toggleReplace(open: Boolean? = null) {
        _uiState.update { state ->
            val shouldOpen = open ?: !state.searchState.isReplaceOpen
            state.copy(searchState = state.searchState.copy(isReplaceOpen = shouldOpen))
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            val updated = state.searchState.copy(searchQuery = query)
                .findMatches(state.activeTab?.content ?: "")
            state.copy(searchState = updated)
        }
    }

    fun setReplaceQuery(query: String) {
        _uiState.update { state ->
            state.copy(searchState = state.searchState.copy(replaceQuery = query))
        }
    }

    fun toggleCaseSensitive() {
        _uiState.update { state ->
            val updated = state.searchState.copy(caseSensitive = !state.searchState.caseSensitive)
                .findMatches(state.activeTab?.content ?: "")
            state.copy(searchState = updated)
        }
    }

    fun toggleRegex() {
        _uiState.update { state ->
            val updated = state.searchState.copy(isRegex = !state.searchState.isRegex)
                .findMatches(state.activeTab?.content ?: "")
            state.copy(searchState = updated)
        }
    }

    fun toggleWholeWord() {
        _uiState.update { state ->
            val updated = state.searchState.copy(wholeWord = !state.searchState.wholeWord)
                .findMatches(state.activeTab?.content ?: "")
            state.copy(searchState = updated)
        }
    }

    fun nextMatch() {
        _uiState.update { state ->
            state.copy(searchState = state.searchState.nextMatch())
        }
    }

    fun previousMatch() {
        _uiState.update { state ->
            state.copy(searchState = state.searchState.previousMatch())
        }
    }

    fun replaceCurrent() {
        val active = _uiState.value.activeTab ?: return
        val result = _uiState.value.searchState.replaceCurrent(active.content) ?: return
        val (newText, newState) = result

        _uiState.update { state ->
            val updatedTab = active.copy(
                content = newText,
                isModified = true,
                undoStack = (active.undoStack + active.content).takeLast(50)
            )
            val updatedTabs = state.tabs.toMutableList()
            updatedTabs[state.activeTabIndex] = updatedTab
            state.copy(tabs = updatedTabs, searchState = newState)
        }
    }

    fun replaceAll() {
        val active = _uiState.value.activeTab ?: return
        val (newText, newState) = _uiState.value.searchState.replaceAll(active.content)

        _uiState.update { state ->
            val updatedTab = active.copy(
                content = newText,
                isModified = true,
                undoStack = (active.undoStack + active.content).takeLast(50)
            )
            val updatedTabs = state.tabs.toMutableList()
            updatedTabs[state.activeTabIndex] = updatedTab
            state.copy(tabs = updatedTabs, searchState = newState)
        }
    }

    fun toggleWordWrap() {
        _uiState.update { it.copy(isWordWrapEnabled = !it.isWordWrapEnabled) }
    }

    fun increaseFontSize() {
        _uiState.update { state ->
            val newSize = (state.fontSizeSp + 1f).coerceAtMost(28f)
            state.copy(fontSizeSp = newSize)
        }
    }

    fun decreaseFontSize() {
        _uiState.update { state ->
            val newSize = (state.fontSizeSp - 1f).coerceAtLeast(10f)
            state.copy(fontSizeSp = newSize)
        }
    }

    fun resetFontSize() {
        _uiState.update { it.copy(fontSizeSp = 14f) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(messageSnackbar = null) }
    }
}
