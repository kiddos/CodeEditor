package app.kiddos.codeeditor.data

enum class ProgrammingLanguage(
    val displayName: String,
    val badge: String,
    val extensions: List<String>
) {
    CPP("C / C++", "C++", listOf("cpp", "c", "h", "hpp", "cc", "cxx")),
    PYTHON("Python", "Py", listOf("py", "pyw")),
    RUST("Rust", "Rs", listOf("rs")),
    JAVA("Java", "Jv", listOf("java")),
    KOTLIN("Kotlin", "Kt", listOf("kt", "kts")),
    JAVASCRIPT("JavaScript", "JS", listOf("js", "jsx", "mjs")),
    TYPESCRIPT("TypeScript", "TS", listOf("ts", "tsx")),
    HTML("HTML", "HTML", listOf("html", "htm")),
    JSON("JSON", "JSON", listOf("json")),
    MARKDOWN("Markdown", "MD", listOf("md", "markdown")),
    SQL("SQL", "SQL", listOf("sql")),
    PLAIN_TEXT("Plain Text", "TXT", listOf("txt", "log"));

    companion object {
        fun fromFileName(fileName: String): ProgrammingLanguage {
            val extension = fileName.substringAfterLast('.', "").lowercase()
            if (extension.isEmpty()) return PLAIN_TEXT
            return entries.firstOrNull { it.extensions.contains(extension) } ?: PLAIN_TEXT
        }
    }
}
