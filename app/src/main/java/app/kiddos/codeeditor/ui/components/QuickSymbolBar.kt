package app.kiddos.codeeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardTab
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kiddos.codeeditor.data.EditorTheme

@Composable
fun QuickSymbolBar(
    theme: EditorTheme,
    onInsertSymbol: (String) -> Unit,
    onIndent: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val symbols = listOf(
        "(", ")", "{", "}", "[", "]",
        ",", ".", ";", ":", "\"", "'", "`",
        "=", "<", ">", "+", "-", "*", "/", "%",
        "&", "|", "!", "^", "~",
        "?", "$", "#", "@", "_", "\\"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp),
        color = theme.gutterBackground,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab key
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.currentLineBackground)
                    .clickable(onClick = onIndent)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardTab,
                        contentDescription = "Tab",
                        tint = theme.keywordColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " Tab",
                        color = theme.foreground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Undo
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (canUndo) theme.currentLineBackground else theme.gutterBackground)
                    .clickable(enabled = canUndo, onClick = onUndo)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) theme.foreground else theme.gutterText,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Redo
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (canRedo) theme.currentLineBackground else theme.gutterBackground)
                    .clickable(enabled = canRedo, onClick = onRedo)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) theme.foreground else theme.gutterText,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Code symbols
            symbols.forEach { symbol ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.currentLineBackground)
                        .clickable { onInsertSymbol(symbol) }
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol,
                        color = theme.foreground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
