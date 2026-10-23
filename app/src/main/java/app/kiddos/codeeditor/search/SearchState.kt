package app.kiddos.codeeditor.search

import java.util.regex.Pattern

data class SearchState(
    val isSearchOpen: Boolean = false,
    val isReplaceOpen: Boolean = false,
    val searchQuery: String = "",
    val replaceQuery: String = "",
    val caseSensitive: Boolean = false,
    val isRegex: Boolean = false,
    val wholeWord: Boolean = false,
    val matches: List<IntRange> = emptyList(),
    val currentMatchIndex: Int = -1
) {
    val totalMatches: Int get() = matches.size

    val matchDisplayString: String
        get() = when {
            searchQuery.isEmpty() -> ""
            matches.isEmpty() -> "No match"
            else -> "${currentMatchIndex + 1} of ${matches.size}"
        }

    fun findMatches(text: String): SearchState {
        if (searchQuery.isEmpty() || text.isEmpty()) {
            return copy(matches = emptyList(), currentMatchIndex = -1)
        }

        val found = mutableListOf<IntRange>()
        try {
            val flags = if (caseSensitive) 0 else Pattern.CASE_INSENSITIVE
            val patternString = when {
                isRegex -> searchQuery
                wholeWord -> "\\b${Pattern.quote(searchQuery)}\\b"
                else -> Pattern.quote(searchQuery)
            }
            val pattern = Pattern.compile(patternString, flags)
            val matcher = pattern.matcher(text)

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                if (end > start) {
                    found.add(start until end)
                }
            }
        } catch (_: Exception) {
            // Invalid regex or matching error
            return copy(matches = emptyList(), currentMatchIndex = -1)
        }

        val newIndex = if (found.isEmpty()) {
            -1
        } else if (currentMatchIndex in found.indices) {
            currentMatchIndex
        } else {
            0
        }

        return copy(matches = found, currentMatchIndex = newIndex)
    }

    fun nextMatch(): SearchState {
        if (matches.isEmpty()) return this
        val nextIdx = (currentMatchIndex + 1) % matches.size
        return copy(currentMatchIndex = nextIdx)
    }

    fun previousMatch(): SearchState {
        if (matches.isEmpty()) return this
        val prevIdx = if (currentMatchIndex <= 0) matches.size - 1 else currentMatchIndex - 1
        return copy(currentMatchIndex = prevIdx)
    }

    fun replaceCurrent(currentText: String): Pair<String, SearchState>? {
        if (matches.isEmpty() || currentMatchIndex !in matches.indices) return null
        val targetRange = matches[currentMatchIndex]
        val before = currentText.substring(0, targetRange.first)
        val after = currentText.substring(targetRange.last + 1)
        val newText = before + replaceQuery + after

        val updatedState = findMatches(newText)
        return Pair(newText, updatedState)
    }

    fun replaceAll(currentText: String): Pair<String, SearchState> {
        if (searchQuery.isEmpty() || matches.isEmpty()) return Pair(currentText, this)

        val newText = try {
            val flags = if (caseSensitive) 0 else Pattern.CASE_INSENSITIVE
            val patternString = when {
                isRegex -> searchQuery
                wholeWord -> "\\b${Pattern.quote(searchQuery)}\\b"
                else -> Pattern.quote(searchQuery)
            }
            val pattern = Pattern.compile(patternString, flags)
            pattern.matcher(currentText).replaceAll(replaceQuery)
        } catch (_: Exception) {
            currentText
        }

        val updatedState = findMatches(newText)
        return Pair(newText, updatedState)
    }
}
