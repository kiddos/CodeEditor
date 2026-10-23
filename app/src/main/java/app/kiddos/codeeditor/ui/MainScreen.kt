package app.kiddos.codeeditor.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.WrapText
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kiddos.codeeditor.ui.components.CodeEditorView
import app.kiddos.codeeditor.ui.components.EncodingDialog
import app.kiddos.codeeditor.ui.components.LanguageDialog
import app.kiddos.codeeditor.ui.components.NewFileDialog
import app.kiddos.codeeditor.ui.components.QuickSymbolBar
import app.kiddos.codeeditor.ui.components.SearchBar
import app.kiddos.codeeditor.ui.components.StatusBar
import app.kiddos.codeeditor.ui.components.TabBar
import app.kiddos.codeeditor.ui.components.ThemeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: EditorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog states
    var showThemeDialog by remember { mutableStateOf(false) }
    var showEncodingDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    // SAF Document Launchers
    val openFilesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            viewModel.openFile(context, uri)
        }
    }

    val saveAsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null) {
            viewModel.saveCurrentFile(context, uri)
        }
    }

    // Show Snackbars
    LaunchedEffect(uiState.messageSnackbar) {
        uiState.messageSnackbar?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    val activeTab = uiState.activeTab
    val theme = uiState.theme

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(
                            text = "CodeEditor",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.foreground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.gutterBackground,
                    titleContentColor = theme.foreground
                ),
                actions = {
                    // Search Button
                    IconButton(onClick = { viewModel.toggleSearch() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (uiState.searchState.isSearchOpen) theme.keywordColor else theme.foreground
                        )
                    }

                    // Open Files Button
                    IconButton(onClick = { openFilesLauncher.launch(arrayOf("*/*")) }) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Open File",
                            tint = theme.foreground
                        )
                    }

                    // Save Button
                    IconButton(onClick = {
                        if (activeTab?.uri != null) {
                            viewModel.saveCurrentFile(context)
                        } else {
                            val defaultName = activeTab?.title ?: "untitled.txt"
                            saveAsLauncher.launch(defaultName)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = if (activeTab?.isModified == true) theme.typeColor else theme.foreground
                        )
                    }

                    // Theme Button
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme",
                            tint = theme.foreground
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = theme.foreground
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.background(theme.gutterBackground)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "New File...",
                                        color = theme.foreground
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = theme.keywordColor
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    showNewFileDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Save As...",
                                        color = theme.foreground
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        tint = theme.typeColor
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    val defaultName = activeTab?.title ?: "untitled.txt"
                                    saveAsLauncher.launch(defaultName)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (uiState.isWordWrapEnabled) "Disable Word Wrap" else "Enable Word Wrap",
                                        color = theme.foreground
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.WrapText,
                                        contentDescription = null,
                                        tint = theme.stringColor
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.toggleWordWrap()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Encoding (${activeTab?.encoding?.displayName() ?: "UTF-8"})",
                                        color = theme.foreground
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = null,
                                        tint = theme.typeColor
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    showEncodingDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Increase Font (${uiState.fontSizeSp.toInt()}sp)",
                                        color = theme.foreground
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = theme.stringColor
                                    )
                                },
                                onClick = {
                                    viewModel.increaseFontSize()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Decrease Font",
                                        color = theme.foreground
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = theme.commentColor
                                    )
                                },
                                onClick = {
                                    viewModel.decreaseFontSize()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Syntax Language (${activeTab?.language?.displayName ?: "Plain Text"})",
                                        color = theme.foreground
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = theme.keywordColor
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    showLanguageDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(theme.background)
        ) {
            // Tab Bar for Multiple Files
            TabBar(
                tabs = uiState.tabs,
                activeTabIndex = uiState.activeTabIndex,
                theme = theme,
                onTabSelected = { viewModel.selectTab(it) },
                onTabClosed = { viewModel.closeTab(it) },
                onNewTabClick = { showNewFileDialog = true }
            )

            // Search Bar (Animated)
            AnimatedVisibility(
                visible = uiState.searchState.isSearchOpen,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                SearchBar(
                    searchState = uiState.searchState,
                    theme = theme,
                    onQueryChanged = { viewModel.setSearchQuery(it) },
                    onReplaceChanged = { viewModel.setReplaceQuery(it) },
                    onNextMatch = { viewModel.nextMatch() },
                    onPreviousMatch = { viewModel.previousMatch() },
                    onReplaceCurrent = { viewModel.replaceCurrent() },
                    onReplaceAll = { viewModel.replaceAll() },
                    onToggleCaseSensitive = { viewModel.toggleCaseSensitive() },
                    onToggleRegex = { viewModel.toggleRegex() },
                    onToggleWholeWord = { viewModel.toggleWholeWord() },
                    onToggleReplace = { viewModel.toggleReplace() },
                    onClose = { viewModel.toggleSearch(false) }
                )
            }

            // Code Editor with Line Numbers Gutter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (activeTab != null) {
                    CodeEditorView(
                        content = activeTab.content,
                        language = activeTab.language,
                        theme = theme,
                        searchState = uiState.searchState,
                        isWordWrapEnabled = uiState.isWordWrapEnabled,
                        fontSizeSp = uiState.fontSizeSp,
                        onContentChanged = { newText, cursorPos ->
                            viewModel.updateContent(newText, cursorPos)
                        }
                    )
                }
            }

            // Quick Symbol and Indent Bar
            QuickSymbolBar(
                theme = theme,
                onInsertSymbol = { viewModel.insertTextAtCursor(it) },
                onIndent = { viewModel.indent() },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                canUndo = activeTab?.undoStack?.isNotEmpty() == true,
                canRedo = activeTab?.redoStack?.isNotEmpty() == true
            )

            // Status Bar at the bottom
            StatusBar(
                tab = activeTab,
                theme = theme,
                onLanguageClick = { showLanguageDialog = true },
                onEncodingClick = { showEncodingDialog = true },
                onThemeClick = { showThemeDialog = true }
            )
        }
    }

    // Theme Switcher Dialog
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = theme,
            onThemeSelected = { viewModel.setTheme(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    // Encoding Dialog (Reload with encoding or Save with encoding)
    if (showEncodingDialog && activeTab != null) {
        EncodingDialog(
            currentEncoding = activeTab.encoding,
            theme = theme,
            onDismiss = { showEncodingDialog = false },
            onReloadWithEncoding = { charset ->
                viewModel.reloadWithEncoding(charset, context)
            },
            onSetEncoding = { charset ->
                viewModel.setEncoding(charset)
            }
        )
    }

    // Language Selector Dialog
    if (showLanguageDialog && activeTab != null) {
        LanguageDialog(
            currentLanguage = activeTab.language,
            theme = theme,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // New File Dialog
    if (showNewFileDialog) {
        NewFileDialog(
            theme = theme,
            onDismiss = { showNewFileDialog = false },
            onEmptyFile = { viewModel.addNewTab() },
            onSampleFile = { lang -> viewModel.addSampleTab(lang) }
        )
    }
}
