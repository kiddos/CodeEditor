package app.kiddos.codeeditor.data

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

data class EncodingOption(
    val displayName: String,
    val charset: Charset
)

object EncodingHelper {
    val supportedEncodings: List<EncodingOption> by lazy {
        val list = mutableListOf(
            EncodingOption("UTF-8", StandardCharsets.UTF_8),
            EncodingOption("UTF-16LE", StandardCharsets.UTF_16LE),
            EncodingOption("UTF-16BE", StandardCharsets.UTF_16BE),
            EncodingOption("ISO-8859-1 (Latin-1)", StandardCharsets.ISO_8859_1),
            EncodingOption("US-ASCII", StandardCharsets.US_ASCII)
        )

        // Try to add extended charsets safely (if supported by JVM / Android runtime)
        val candidateCharsets = listOf(
            "windows-1252" to "Windows-1252",
            "GBK" to "GBK (Chinese Simplified)",
            "GB2312" to "GB2312 (Chinese Simplified)",
            "Big5" to "Big5 (Chinese Traditional)",
            "Shift_JIS" to "Shift_JIS (Japanese)",
            "EUC-JP" to "EUC-JP (Japanese)",
            "EUC-KR" to "EUC-KR (Korean)"
        )

        for ((charsetName, label) in candidateCharsets) {
            try {
                if (Charset.isSupported(charsetName)) {
                    list.add(EncodingOption(label, Charset.forName(charsetName)))
                }
            } catch (_: Exception) {
                // Ignore unsupported charsets on specific devices
            }
        }

        list
    }

    fun decodeBytes(bytes: ByteArray, charset: Charset): String {
        return try {
            // Strip UTF-8 BOM if present
            if (charset == StandardCharsets.UTF_8 && bytes.size >= 3 &&
                bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()
            ) {
                String(bytes, 3, bytes.size - 3, charset)
            } else {
                String(bytes, charset)
            }
        } catch (_: Exception) {
            String(bytes, StandardCharsets.UTF_8)
        }
    }

    fun encodeString(text: String, charset: Charset): ByteArray {
        return text.toByteArray(charset)
    }

    fun detectCharset(bytes: ByteArray): Charset {
        if (bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
            return StandardCharsets.UTF_8
        }
        if (bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xFE.toByte()) {
            return StandardCharsets.UTF_16LE
        }
        if (bytes.size >= 2 && bytes[0] == 0xFE.toByte() && bytes[1] == 0xFF.toByte()) {
            return StandardCharsets.UTF_16BE
        }
        return StandardCharsets.UTF_8
    }
}
