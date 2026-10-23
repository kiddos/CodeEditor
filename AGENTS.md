# Developer & Agent Guide for CodeEditor

Welcome to the `CodeEditor` project. This guide is tailored for AI agents and human developers working on this repository. It documents the build system, testing commands, directory structure, architecture, and coding conventions.

---

## 1. Project Overview

`CodeEditor` is a modern Android text and code editor written in Kotlin using **Jetpack Compose** and **Material 3**.

### Key Features
- **Multi-language Syntax Highlighting**: C/C++, Python, Rust, Java, Kotlin, JavaScript, TypeScript, HTML, JSON, Markdown, and SQL.
- **Multi-Tab Editor**: Open multiple files simultaneously, manage tabs, and track modification state.
- **Search & Replace**: Regex support, whole-word matching, case sensitivity toggle, incremental match navigation, and replace / replace-all.
- **Character Encodings**: Auto-detection (UTF-8 BOM, UTF-16 LE/BE), multi-charset decoding/reloading/saving (UTF-8, ISO-8859-1, GBK, Shift_JIS, Big5, etc.).
- **Multiple Color Themes**: One Dark, Dracula, Monokai Pro, Nord, Solarized Dark, Solarized Light, and GitHub Light.
- **Storage Access Framework (SAF)**: Modern Android scoped storage file picker (`OpenMultipleDocuments`, `CreateDocument`) without broad storage permissions.
- **Quick Accessory Bar & Customization**: Quick symbol bar for fast coding symbols, undo/redo stack, font sizing, and word wrap.

---

## 2. Build System & Environment

The project is built with **Gradle** using the Kotlin DSL (`.gradle.kts`) and the Gradle Version Catalog (`gradle/libs.versions.toml`).

### Toolchain Requirements
- **Gradle Version**: `9.3.1` (configured via Gradle Wrapper)
- **Android Gradle Plugin (AGP)**: `9.1.1`
- **Kotlin Version**: `2.2.10`
- **Compose Compiler**: Kotlin Compose Plugin (`org.jetbrains.kotlin.plugin.compose`)
- **JDK Requirement**: **JDK 17** or **JDK 21** (AGP 9.1+ requires minimum Java 17; foojay toolchain resolver convention is enabled in `settings.gradle.kts`)
- **Compile SDK**: `36` (release 36, minorApiLevel 1)
- **Target SDK**: `36`
- **Min SDK**: `28` (Android 9.0 Pie)
- **Source / Target Compatibility**: `JavaVersion.VERSION_11`

### Configuration Files
- `settings.gradle.kts`: Root settings, plugin repositories (`google`, `mavenCentral`, `gradlePluginPortal`), dependency repositories (`google`, `mavenCentral` with `FAIL_ON_PROJECT_REPOS`), and module definitions (`:app`).
- `build.gradle.kts`: Root-level plugin alias declarations without application (`apply false`).
- `gradle/libs.versions.toml`: Centralized version catalog for dependencies and plugins.
- `app/build.gradle.kts`: Application module configuration (SDKs, build types, Jetpack Compose features, dependencies).
- `gradle.properties`: JVM arguments (`-Xmx2048m -Dfile.encoding=UTF-8`) and official Kotlin code style.
- `local.properties`: Machine-specific Android SDK path (`sdk.dir`).

---

## 3. Test & Build Commands

Always run Gradle commands through the wrapper script (`./gradlew` on Linux/macOS or `gradlew.bat` on Windows).

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Full build (compilation and checks)
./gradlew build

# Clean build artifacts
./gradlew clean
```

Debug APK output location: `app/build/outputs/apk/debug/app-debug.apk`

### Testing Commands

#### Unit Tests (Host JVM)
Fast JVM unit tests run directly on the host machine without requiring an Android emulator or physical device.

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run a specific test class
./gradlew testDebugUnitTest --tests "app.kiddos.codeeditor.EditorLogicTest"

# Run a specific test method
./gradlew testDebugUnitTest --tests "app.kiddos.codeeditor.EditorLogicTest.testSearchNavigation"
./gradlew testDebugUnitTest --tests "app.kiddos.codeeditor.EditorLogicTest.testEncodingConversion"

# Run all test tasks across variants
./gradlew test
```

Unit test reports are written to:
- HTML Report: `app/build/reports/tests/testDebugUnitTest/index.html`
- JUnit XML: `app/build/test-results/testDebugUnitTest/TEST-*.xml`

#### Instrumented Tests (Device / Emulator)
Tests requiring an active Android runtime or device context:

```bash
# Run instrumented tests on connected device/emulator
./gradlew connectedDebugAndroidTest

# Run all verification checks (unit tests + lint)
./gradlew check
```

#### Lint & Static Analysis

```bash
# Run Android lint on debug variant
./gradlew lintDebug

# Run lint on all variants
./gradlew lint
```

---

## 4. Directory Structure

```
CodeEditor/
├── AGENTS.md                      # Agent and developer reference guide (this file)
├── build.gradle.kts               # Top-level build configuration
├── settings.gradle.kts            # Project settings, repositories, and plugins
├── gradle.properties              # JVM daemon memory configuration & Kotlin code style
├── local.properties               # Local Android SDK path (sdk.dir)
├── gradlew / gradlew.bat          # Gradle wrapper executable scripts
├── gradle/
│   ├── libs.versions.toml         # Gradle Version Catalog (dependencies & versions)
│   ├── gradle-daemon-jvm.properties
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties # Gradle 9.3.1 distribution URL & checksum
└── app/
    ├── build.gradle.kts           # App module build configuration, SDK targets, dependencies
    ├── proguard-rules.pro         # Proguard/R8 optimization and obfuscation rules
    └── src/
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

---

## 5. Architectural Patterns

### MVVM with Unidirectional Data Flow (UDF)
- **Model**: Immutable data structures (`EditorTab`, `EditorTheme`, `SearchState`, `ProgrammingLanguage`).
- **ViewModel**: `EditorViewModel` manages all application state in a single `MutableStateFlow<EditorUiState>`, exposing a public read-only `StateFlow<EditorUiState>`.
- **View**: Declarative composables (`MainScreen`, `CodeEditorView`, `TabBar`, etc.) consume `EditorUiState` and emit user actions via callbacks to the ViewModel.
- **State Updates**: State modifications use `_uiState.update { state -> state.copy(...) }` to ensure thread-safe atomic updates.

### Decoupled Core Logic
- Logic in `syntax/`, `search/`, and `data/` (`EncodingHelper`, `SearchState`, `SyntaxHighlighter`) is intentionally decoupled from Android Android/Context APIs.
- This allows core algorithmic logic (regex search, replace, syntax highlighting parsing, encoding conversion) to be thoroughly tested using pure host-side JVM unit tests (`EditorLogicTest.kt`) without requiring an emulator.

### Non-destructive Syntax Highlighting
- Code syntax coloring and search match highlighting are applied using Compose's `VisualTransformation` (`CodeVisualTransformation`) producing `AnnotatedString` with `SpanStyle`.
- The underlying text buffer remains plain text, avoiding text mutation bugs or synchronization overhead.

---

## 6. Coding Conventions & Guidelines

### Language & Formatting
- **Code Style**: Strictly follow official Kotlin coding conventions (`kotlin.code.style=official` in `gradle.properties`).
- **Naming Conventions**:
  - `PascalCase`: Classes, interfaces, objects, and `@Composable` functions (e.g., `EditorViewModel`, `CodeEditorView`, `SearchState`).
  - `camelCase`: Functions, properties, local variables, and method parameters (e.g., `activeTabIndex`, `toggleCaseSensitive()`).
  - `UPPER_SNAKE_CASE`: Enum constants (e.g., `ProgrammingLanguage.CPP`, `ProgrammingLanguage.PLAIN_TEXT`).
  - Constants in companion objects: `PascalCase` or `camelCase` depending on object vs value semantics (e.g., `EditorTheme.OneDark`).

### Compose Best Practices
- **Stateless Composables**: Pass state in and propagate events out using lambda callbacks (e.g., `onTabSelected: (Int) -> Unit`).
- **Modifier Parameter**: Every public `@Composable` accepting layout properties should accept `modifier: Modifier = Modifier` as a parameter and apply it to its root element.
- **Remembering Computations**: Wrap expensive calculations (such as line counts or regex operations) in `remember(keys) { ... }`.
- **Material 3**: Use Material 3 components (`androidx.compose.material3.*`) and color schemes.

### File Handling & Android Storage
- Use Android's **Storage Access Framework (SAF)** with `rememberLauncherForActivityResult` and `ActivityResultContracts.OpenMultipleDocuments` / `ActivityResultContracts.CreateDocument`.
- Never hardcode filesystem paths for user documents.
- Always use `context.contentResolver.openInputStream(uri)?.use { ... }` and `openOutputStream(uri)?.use { ... }` to guarantee stream closure.

### Error Handling & User Feedback
- Guard against invalid user input (e.g., invalid regex queries in `SearchState` or unsupported charsets in `EncodingHelper`) using safe `try-catch` blocks and sensible fallbacks.
- Communicate asynchronous or I/O results (file opened, file saved, encoding switched, errors) via `EditorUiState.messageSnackbar` displayed by `SnackbarHost`.

### Adding New Features
1. **New Language Support**:
   - Add the enum entry in `ProgrammingLanguage.kt` with appropriate file extensions and short badge string.
   - Define keyword and type sets in `LanguageRules.kt`.
   - Update `SyntaxHighlighter.kt` if the language introduces unique comment or string syntaxes (e.g., custom multiline string formats).
   - Add test coverage in `EditorLogicTest.kt`.
2. **New Themes**:
   - Add a new `EditorTheme` constant in `EditorTheme.Companion` in `EditorTheme.kt`.
   - Add the new theme instance to `EditorTheme.allThemes`.
   - Add verification to `EditorLogicTest.testThemeCollection()`.
3. **Tests**:
   - Whenever modifying search, syntax parsing, or encoding logic, run `./gradlew testDebugUnitTest` to verify test suite health.
