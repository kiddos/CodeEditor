package app.kiddos.codeeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import app.kiddos.codeeditor.data.EditorTab
import app.kiddos.codeeditor.data.EditorTheme

@Composable
fun TabBar(
    tabs: List<EditorTab>,
    activeTabIndex: Int,
    theme: EditorTheme,
    onTabSelected: (Int) -> Unit,
    onTabClosed: (Int) -> Unit,
    onNewTabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        color = theme.gutterBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isActive = index == activeTabIndex
                TabItem(
                    tab = tab,
                    isActive = isActive,
                    theme = theme,
                    onClick = { onTabSelected(index) },
                    onClose = { onTabClosed(index) }
                )
            }

            // Add Tab button
            IconButton(
                onClick = onNewTabClick,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Tab",
                    tint = theme.gutterText
                )
            }
        }
    }
}

@Composable
fun TabItem(
    tab: EditorTab,
    isActive: Boolean,
    theme: EditorTheme,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) theme.background else theme.gutterBackground
    val textColor = if (isActive) theme.foreground else theme.gutterText

    Box(
        modifier = modifier
            .padding(top = 4.dp, bottom = 2.dp, start = 4.dp, end = 2.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(backgroundColor)
            .then(
                if (isActive) {
                    Modifier.drawBehind {
                        drawRect(
                            color = theme.keywordColor,
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, 2.5.dp.toPx())
                        )
                    }
                } else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.currentLineBackground)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = tab.language.badge,
                    color = theme.keywordColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // File Title
            Text(
                text = tab.title,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Modified dot indicator
            if (tab.isModified) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(theme.typeColor)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Close Tab icon
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Tab",
                    tint = theme.gutterText,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
