# AGENTS.md

This file provides instructions and configuration details for autonomous coding agents modifying this repository. Follow these guidelines to ensure accurate and consistent modifications.

## 1. Project Overview & Architecture

**CodeEditor** is a modern, fast, and privacy-focused text and code editor for Android.

*   **Module Structure:** The project is currently a monolithic app with a single `:app` module.
*   **Module Dependency Rules:** If new feature modules are added in the future, feature modules must not depend on other feature modules. All features should depend on core modules.
*   **Architecture Pattern:** The application follows the **MVVM (Model-View-ViewModel)** architectural pattern combined with **Unidirectional Data Flow (UDF)** managed via `StateFlow` and `SharedFlow`.
*   **Decoupled Core:** Core logic (syntax highlighting, search/replace, encoding engines) is strictly decoupled from Android framework dependencies to enable fast host JVM testing.

### Directory Structure
```
app/src/
├── main/
│   ├── AndroidManifest.xml # Application manifest, activity declarations, app theme
│   ├── java/app/kiddos/codeeditor/
│   │   ├── MainActivity.kt # Main entry Activity with edge-to-edge Compose host
│   │   ├── data/           # Domain models, enums, and data helpers
│   │   │   ├── EditorTab.kt           # Immutable tab state (URI, text, language, undo/redo)
│   │   │   ├── EditorTheme.kt         # Theme definitions and color palettes
│   │   │   ├── EncodingHelper.kt      # Charset detection, UTF-8 BOM, encoding conversions
│   │   │   ├── ProgrammingLanguage.kt  # Supported languages, badge labels, and file extensions
│   │   │   └── SampleCodes.kt         # Pre-loaded starter code templates
│   │   ├── search/         # Text search and replace engine
│   │   │   └── SearchState.kt         # Search query state, regex matching, match navigation, replace
│   │   ├── syntax/         # Syntax highlighting engine
│   │   │   ├── LanguageRules.kt       # Keywords and built-in type sets for supported languages
│   │   │   └── SyntaxHighlighter.kt   # Regex token scanner & Compose CodeVisualTransformation
│   │   └── ui/             # Jetpack Compose UI layer
│   │       ├── EditorViewModel.kt     # Main ViewModel: tabs, undo/redo, file I/O, search state
│   │       ├── MainScreen.kt          # Root Compose scaffold, top bar, SAF launchers, dialog triggers
│   │       ├── components/            # Reusable UI components
│   │       │   ├── CodeEditorView.kt  # Editor viewport with line number gutter & visual transformation
│   │       │   ├── Dialogs.kt         # Theme, encoding, language, and new file dialogs
│   │       │   ├── QuickSymbolBar.kt  # Quick character insertion row for programming symbols
│   │       │   ├── SearchBar.kt       # Floating search and replace bar with match controls
│   │       │   ├── StatusBar.kt       # Bottom status bar: line/col, length, encoding, language, theme
│   │       │   └── TabBar.kt          # Multi-tab strip with selection, close, and add actions
│   │       └── theme/                 # App-wide Material 3 theme styling
│   │           ├── Color.kt           # Baseline palette tokens
│   │           ├── Theme.kt           # Dynamic / dark / light MaterialTheme configuration
│   │           └── Type.kt            # Typography definitions
│   └── res/                           # Android resources
│       ├── drawable/                  # Vector drawables and launcher backgrounds
│       ├── mipmap-*/                  # App launcher webp icons
│       ├── values/                    # strings.xml, colors.xml, themes.xml
│       └── xml/                       # backup_rules.xml, data_extraction_rules.xml
├── test/                              # Local JVM unit tests
│   └── java/app/kiddos/codeeditor/
│       ├── EditorLogicTest.kt         # Comprehensive tests: syntax, search/replace, encodings
│       └── ExampleUnitTest.kt         # Baseline unit test
└── androidTest/                       # Instrumented Android tests
    └── java/app/kiddos/codeeditor/
        └── ExampleInstrumentedTest.kt # Context-based instrumentation test
```

## 2. Environment & Build Commands

The project uses Gradle with the Kotlin DSL (`.gradle.kts`) and a version catalog (`gradle/libs.versions.toml`).

*   **Required JDK Version:** JDK 17 (foojay toolchain resolver convention is enabled in `settings.gradle.kts`).
*   **JVM Target (Source/Target Compatibility):** Java 11 (JavaVersion.VERSION_11).
*   **Android SDK:** Compile SDK 36, Target SDK 36, Min SDK 28.
*   **Kotlin Version:** 2.2.10.
*   **Android Gradle Plugin (AGP):** 9.1.1.

### Gradle Commands

Run all Gradle commands through the wrapper (`./gradlew` on Linux/macOS or `gradlew.bat` on Windows).

*   **Build Debug APK:** `./gradlew assembleDebug`
*   **Build Release APK:** `./gradlew assembleRelease`
*   **Clean Build Artifacts:** `./gradlew clean`
*   **Full Build (compilation and checks):** `./gradlew build`

## 3. Testing & Verification Workflow

### Unit Tests (JVM)
The project uses **JUnit 4** for fast unit tests that run directly on the host machine.
*   **Run all unit tests:** `./gradlew testDebugUnitTest`
*   **Run a specific test class:** `./gradlew testDebugUnitTest --tests "app.kiddos.codeeditor.EditorLogicTest"`
*   **Run a specific test method:** `./gradlew testDebugUnitTest --tests "app.kiddos.codeeditor.EditorLogicTest.testSearchNavigation"`

*(Agents can and should run JVM unit tests directly.)*

### Instrumented Tests (Android)
The project uses **JUnit 4** and **Espresso** for instrumented tests requiring an active Android emulator or physical device.
*   **Run instrumented tests:** `./gradlew connectedDebugAndroidTest`

*(Note: Instrumented tests require a connected device/emulator.)*

### Static Analysis, Linting & Formatting
The project uses the standard **Android Lint**.
*   **Run Android lint on debug variant:** `./gradlew lintDebug`
*   **Run lint on all variants:** `./gradlew lint`

*(There are currently no additional formatters like ktlint, detekt, or Spotless configured, so rely on standard Kotlin coding conventions `kotlin.code.style=official` in `gradle.properties`.)*

## 4. Kotlin & Android Coding Conventions

### UI Layer
*   **Jetpack Compose:** Use Jetpack Compose for all UI components. Avoid traditional XML Views.
*   **Material 3:** Use Material 3 themes and components.
*   **State Hoisting:** Pass state down to composables and propagate events up using lambda callbacks (e.g., `onTabSelected: (Int) -> Unit`).
*   **Modifiers:** Every public `@Composable` accepting layout properties should accept `modifier: Modifier = Modifier` as a parameter and apply it to its root element.
*   **VisualTransformation:** Used for non-destructive syntax highlighting (e.g., `CodeVisualTransformation`).
*   **Remembering Computations**: Wrap expensive calculations (such as line counts or regex operations) in `remember(keys) { ... }`.

### Concurrency & State
*   **Coroutines:** Use Kotlin Coroutines for asynchronous tasks.
*   **Dispatcher Injection:** Dispatchers should be injected into ViewModels or repositories to facilitate testing, rather than hardcoding `Dispatchers.IO` or `Dispatchers.Default`.
*   **State Management:** Use `StateFlow` (for state) and `SharedFlow` (for one-off events) within ViewModels.
*   **Lifecycle-aware collection:** When collecting flows in Compose, use `collectAsStateWithLifecycle()` to ensure proper lifecycle handling and prevent resource leaks.

### Dependency Injection & Libraries
*   **Dependency Injection:** The project currently uses manual dependency injection. Do not add Hilt or Koin unless explicitly requested.
*   **Adding Dependencies:** All new dependencies must be declared in the version catalog first (`gradle/libs.versions.toml`).

### Naming Conventions
*   `PascalCase`: Classes, interfaces, objects, and `@Composable` functions (e.g., `EditorViewModel`, `CodeEditorView`, `SearchState`).
*   `camelCase`: Functions, properties, local variables, and method parameters (e.g., `activeTabIndex`, `toggleCaseSensitive()`).
*   `UPPER_SNAKE_CASE`: Enum constants (e.g., `ProgrammingLanguage.CPP`, `ProgrammingLanguage.PLAIN_TEXT`).
*   Constants in companion objects: `PascalCase` or `camelCase` depending on object vs value semantics (e.g., `EditorTheme.OneDark`).

### File Handling & Storage
*   **File I/O:** Use Android's Storage Access Framework (SAF) (`ActivityResultContracts.OpenMultipleDocuments`, `ActivityResultContracts.CreateDocument`). Do not use broad storage permissions (`MANAGE_EXTERNAL_STORAGE`).
*   **Streams:** Always close streams using `use { ... }` blocks (e.g., `context.contentResolver.openInputStream(uri)?.use { ... }`).
*   **Error Handling:** Use safe `try-catch` blocks for invalid input (e.g. regex or charsets) and communicate asynchronous/IO results to the user via the `SnackbarHost` (e.g., `EditorUiState.messageSnackbar`).

## 5. Agent Guardrails (Do's and Don'ts)

*   **DO NOT manually edit generated files:** Never manually edit files in `build/`, Room schemas, Dagger/Hilt generated code, or other automatically generated paths. Trace issues to their source files and modify those instead.
*   **ALWAYS verify work:** Always run the relevant unit tests (`./gradlew testDebugUnitTest`) and linter tasks (`./gradlew lintDebug`) before finalizing a task to ensure no regressions were introduced.
*   **DO handle resources properly:** Use standard Android `res/` patterns for string resources, drawables, and assets. Do not hardcode strings in Compose files without verifying if they should be extracted to `strings.xml`.
*   **Database Migrations:** If Room is introduced in the future, database schema changes must be accompanied by explicit database migration scripts, and schemas should be exported and verified via tests.

### Adding New Features
When tasked with adding new features, agents and developers must follow these specific steps:

1. **New Language Support**:
   * Add the enum entry in `ProgrammingLanguage.kt` with appropriate file extensions and short badge string.
   * Define keyword and type sets in `LanguageRules.kt`.
   * Update `SyntaxHighlighter.kt` if the language introduces unique comment or string syntaxes (e.g., custom multiline string formats).
   * Add test coverage in `EditorLogicTest.kt`.
2. **New Themes**:
   * Add a new `EditorTheme` constant in `EditorTheme.Companion` in `EditorTheme.kt`.
   * Add the new theme instance to `EditorTheme.allThemes`.
   * Add verification to `EditorLogicTest.testThemeCollection()`.
3. **Tests**:
   * Whenever modifying search, syntax parsing, or encoding logic, run `./gradlew testDebugUnitTest` to verify test suite health.
