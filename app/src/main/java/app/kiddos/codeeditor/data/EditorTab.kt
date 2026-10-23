package app.kiddos.codeeditor.data

import android.net.Uri
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.UUID

data class EditorTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val uri: Uri? = null,
    val content: String = "",
    val language: ProgrammingLanguage = ProgrammingLanguage.PLAIN_TEXT,
    val encoding: Charset = StandardCharsets.UTF_8,
    val isModified: Boolean = false,
    val rawBytes: ByteArray? = null,
    val undoStack: List<String> = emptyList(),
    val redoStack: List<String> = emptyList(),
    val cursorPosition: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EditorTab) return false
        return id == other.id &&
                title == other.title &&
                uri == other.uri &&
                content == other.content &&
                language == other.language &&
                encoding == other.encoding &&
                isModified == other.isModified &&
                cursorPosition == other.cursorPosition
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (uri?.hashCode() ?: 0)
        result = 31 * result + content.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + encoding.hashCode()
        result = 31 * result + isModified.hashCode()
        result = 31 * result + cursorPosition
        return result
    }
}
