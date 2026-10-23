package app.kiddos.codeeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
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
import app.kiddos.codeeditor.data.EditorTab
import app.kiddos.codeeditor.data.EditorTheme

import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset

@Composable
fun StatusBar(
    tab: EditorTab?,
    theme: EditorTheme,
    onLanguageClick: () -> Unit,
    onEncodingClick: () -> Unit,
    onThemeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val (line, col) = if (tab != null && tab.content.isNotEmpty()) {
        val safePos = tab.cursorPosition.coerceIn(0, tab.content.length)
        val lineNum = tab.content.substring(0, safePos).count { it == '\n' } + 1
        val lastNewline = tab.content.lastIndexOf('\n', safePos - 1)
        val colNum = if (lastNewline == -1) safePos + 1 else safePos - lastNewline
        Pair(lineNum, colNum)
    } else {
        Pair(1, 1)
    }

    val totalLines = tab?.content?.count { it == '\n' }?.plus(1) ?: 1
    val totalChars = tab?.content?.length ?: 0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .drawBehind {
                drawLine(
                    color = theme.currentLineBackground,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            },
        color = theme.gutterBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Position and Counts
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ln $line, Col $col",
                    color = theme.gutterText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "•",
                    color = theme.gutterText.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$totalLines L",
                    color = theme.gutterText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Language, Encoding, Theme badges
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.currentLineBackground)
                        .clickable(onClick = onLanguageClick)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tab?.language?.displayName ?: "Plain Text",
                        color = theme.keywordColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Encoding
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.currentLineBackground)
                        .clickable(onClick = onEncodingClick)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tab?.encoding?.displayName() ?: "UTF-8",
                        color = theme.typeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Theme
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.currentLineBackground)
                        .clickable(onClick = onThemeClick)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme",
                            tint = theme.stringColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = theme.name,
                            color = theme.stringColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
