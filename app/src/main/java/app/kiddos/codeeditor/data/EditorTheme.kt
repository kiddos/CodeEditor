package app.kiddos.codeeditor.data

import androidx.compose.ui.graphics.Color

data class EditorTheme(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val foreground: Color,
    val gutterBackground: Color,
    val gutterText: Color,
    val currentLineBackground: Color,
    val selectionBackground: Color,
    val searchMatchBackground: Color,
    val currentSearchMatchBackground: Color,
    val keywordColor: Color,
    val typeColor: Color,
    val stringColor: Color,
    val numberColor: Color,
    val commentColor: Color,
    val preprocessorColor: Color,
    val functionColor: Color,
    val operatorColor: Color
) {
    companion object {
        val OneDark = EditorTheme(
            id = "one_dark",
            name = "One Dark",
            isDark = true,
            background = Color(0xFF282C34),
            foreground = Color(0xFFABB2BF),
            gutterBackground = Color(0xFF21252B),
            gutterText = Color(0xFF5C6370),
            currentLineBackground = Color(0xFF2C313C),
            selectionBackground = Color(0xFF3E4451),
            searchMatchBackground = Color(0x66E5C07B),
            currentSearchMatchBackground = Color(0xBBE5C07B),
            keywordColor = Color(0xFFC678DD),      // Purple
            typeColor = Color(0xFFE5C07B),         // Yellow
            stringColor = Color(0xFF98C379),       // Green
            numberColor = Color(0xFFD19A66),       // Orange
            commentColor = Color(0xFF5C6370),      // Grey
            preprocessorColor = Color(0xFFE06C75), // Red / Coral
            functionColor = Color(0xFF61AFEF),     // Blue
            operatorColor = Color(0xFF56B6C2)      // Cyan
        )

        val MonokaiPro = EditorTheme(
            id = "monokai_pro",
            name = "Monokai Pro",
            isDark = true,
            background = Color(0xFF2D2A2E),
            foreground = Color(0xFFFCFCFA),
            gutterBackground = Color(0xFF221F22),
            gutterText = Color(0xFF727072),
            currentLineBackground = Color(0xFF3A383C),
            selectionBackground = Color(0xFF49464E),
            searchMatchBackground = Color(0x66FFD866),
            currentSearchMatchBackground = Color(0xCCFFD866),
            keywordColor = Color(0xFFFF6188),      // Magenta / Pink
            typeColor = Color(0xFF78DCE8),         // Cyan
            stringColor = Color(0xFFFFD866),       // Yellow
            numberColor = Color(0xFFAB9DF2),       // Violet
            commentColor = Color(0xFF727072),      // Muted grey
            preprocessorColor = Color(0xFFFF6188), // Magenta
            functionColor = Color(0xFFA9DC76),     // Green
            operatorColor = Color(0xFFFF6188)      // Pink
        )

        val Dracula = EditorTheme(
            id = "dracula",
            name = "Dracula",
            isDark = true,
            background = Color(0xFF282A36),
            foreground = Color(0xFFF8F8F2),
            gutterBackground = Color(0xFF1E1F29),
            gutterText = Color(0xFF6272A4),
            currentLineBackground = Color(0xFF343746),
            selectionBackground = Color(0xFF44475A),
            searchMatchBackground = Color(0x66F1FA8C),
            currentSearchMatchBackground = Color(0xCCF1FA8C),
            keywordColor = Color(0xFFFF79C6),      // Pink
            typeColor = Color(0xFF8BE9FD),         // Cyan
            stringColor = Color(0xFFF1FA8C),       // Yellow
            numberColor = Color(0xFFBD93F9),       // Purple
            commentColor = Color(0xFF6272A4),      // Blue grey
            preprocessorColor = Color(0xFFFF5555), // Red
            functionColor = Color(0xFF50FA7B),     // Green
            operatorColor = Color(0xFFFF79C6)      // Pink
        )

        val Nord = EditorTheme(
            id = "nord",
            name = "Nord",
            isDark = true,
            background = Color(0xFF2E3440),
            foreground = Color(0xFFD8DEE9),
            gutterBackground = Color(0xFF242933),
            gutterText = Color(0xFF4C566A),
            currentLineBackground = Color(0xFF3B4252),
            selectionBackground = Color(0xFF434C5E),
            searchMatchBackground = Color(0x66EBCB8B),
            currentSearchMatchBackground = Color(0xCCEBCB8B),
            keywordColor = Color(0xFF81A1C1),      // Frost Blue
            typeColor = Color(0xFF8FBCBB),         // Polar Cyan
            stringColor = Color(0xFFA3BE8C),       // Green
            numberColor = Color(0xFFB48EAD),       // Purple
            commentColor = Color(0xFF616E88),      // Muted slate
            preprocessorColor = Color(0xFFBF616A), // Red
            functionColor = Color(0xFF88C0D0),     // Bright Frost
            operatorColor = Color(0xFF81A1C1)      // Frost Blue
        )

        val SolarizedDark = EditorTheme(
            id = "solarized_dark",
            name = "Solarized Dark",
            isDark = true,
            background = Color(0xFF002B36),
            foreground = Color(0xFF839496),
            gutterBackground = Color(0xFF073642),
            gutterText = Color(0xFF586E75),
            currentLineBackground = Color(0xFF073642),
            selectionBackground = Color(0xFF073642),
            searchMatchBackground = Color(0x66B58900),
            currentSearchMatchBackground = Color(0xCCB58900),
            keywordColor = Color(0xFF859900),      // Green
            typeColor = Color(0xFFB58900),         // Yellow
            stringColor = Color(0xFF2AA198),       // Cyan
            numberColor = Color(0xFFD33682),       // Magenta
            commentColor = Color(0xFF586E75),      // Base01
            preprocessorColor = Color(0xFFCB4B16), // Orange
            functionColor = Color(0xFF268BD2),     // Blue
            operatorColor = Color(0xFF859900)      // Green
        )

        val GitHubLight = EditorTheme(
            id = "github_light",
            name = "GitHub Light",
            isDark = false,
            background = Color(0xFFFFFFFF),
            foreground = Color(0xFF24292E),
            gutterBackground = Color(0xFFF6F8FA),
            gutterText = Color(0xFF959DA5),
            currentLineBackground = Color(0xFFF1F8FF),
            selectionBackground = Color(0xFFCCE5FF),
            searchMatchBackground = Color(0x66FFE58F),
            currentSearchMatchBackground = Color(0xCCFFD666),
            keywordColor = Color(0xFFD73A49),      // Red
            typeColor = Color(0xFF6F42C1),         // Purple
            stringColor = Color(0xFF032F62),       // Dark Blue
            numberColor = Color(0xFF005CC5),       // Blue
            commentColor = Color(0xFF6A737D),      // Slate Grey
            preprocessorColor = Color(0xFFE36209), // Orange
            functionColor = Color(0xFF6F42C1),     // Purple
            operatorColor = Color(0xFFD73A49)      // Red
        )

        val SolarizedLight = EditorTheme(
            id = "solarized_light",
            name = "Solarized Light",
            isDark = false,
            background = Color(0xFFFDF6E3),
            foreground = Color(0xFF657B83),
            gutterBackground = Color(0xFFEEE8D5),
            gutterText = Color(0xFF93A1A1),
            currentLineBackground = Color(0xFFEEE8D5),
            selectionBackground = Color(0xFFE0D8C3),
            searchMatchBackground = Color(0x66B58900),
            currentSearchMatchBackground = Color(0xCCB58900),
            keywordColor = Color(0xFF859900),      // Green
            typeColor = Color(0xFFB58900),         // Yellow
            stringColor = Color(0xFF2AA198),       // Cyan
            numberColor = Color(0xFFD33682),       // Magenta
            commentColor = Color(0xFF93A1A1),      // Grey
            preprocessorColor = Color(0xFFCB4B16), // Orange
            functionColor = Color(0xFF268BD2),     // Blue
            operatorColor = Color(0xFF859900)      // Green
        )

        val allThemes = listOf(
            OneDark,
            MonokaiPro,
            Dracula,
            Nord,
            SolarizedDark,
            GitHubLight,
            SolarizedLight
        )
    }
}
