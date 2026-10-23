package app.kiddos.codeeditor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kiddos.codeeditor.data.EditorTheme
import app.kiddos.codeeditor.search.SearchState

@Composable
fun SearchBar(
    searchState: SearchState,
    theme: EditorTheme,
    onQueryChanged: (String) -> Unit,
    onReplaceChanged: (String) -> Unit,
    onNextMatch: () -> Unit,
    onPreviousMatch: () -> Unit,
    onReplaceCurrent: () -> Unit,
    onReplaceAll: () -> Unit,
    onToggleCaseSensitive: () -> Unit,
    onToggleRegex: () -> Unit,
    onToggleWholeWord: () -> Unit,
    onToggleReplace: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = theme.gutterBackground,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Row 1: Search Input, Navigation, Toggles, Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand / Collapse Replace toggle icon
                IconButton(
                    onClick = onToggleReplace,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (searchState.isReplaceOpen) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Toggle Replace",
                        tint = theme.foreground,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Search Input Field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.background)
                        .border(1.dp, theme.selectionBackground, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchState.searchQuery.isEmpty()) {
                        Text(
                            text = "Find in file...",
                            color = theme.gutterText,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    BasicTextField(
                        value = searchState.searchQuery,
                        onValueChange = onQueryChanged,
                        textStyle = TextStyle(
                            color = theme.foreground,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(theme.keywordColor),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Matches count badge
                if (searchState.matchDisplayString.isNotEmpty()) {
                    Text(
                        text = searchState.matchDisplayString,
                        color = theme.typeColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                // Up (Previous)
                IconButton(
                    onClick = onPreviousMatch,
                    enabled = searchState.matches.isNotEmpty(),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Previous Match",
                        tint = if (searchState.matches.isNotEmpty()) theme.foreground else theme.gutterText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Down (Next)
                IconButton(
                    onClick = onNextMatch,
                    enabled = searchState.matches.isNotEmpty(),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Next Match",
                        tint = if (searchState.matches.isNotEmpty()) theme.foreground else theme.gutterText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Close Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Search",
                        tint = theme.foreground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Options Bar (Case Sensitive, Whole Word, Regex)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(36.dp))

                SearchOptionToggle(
                    label = "Aa",
                    tooltip = "Case Sensitive",
                    isSelected = searchState.caseSensitive,
                    theme = theme,
                    onClick = onToggleCaseSensitive
                )

                Spacer(modifier = Modifier.width(6.dp))

                SearchOptionToggle(
                    label = "\\b",
                    tooltip = "Whole Word",
                    isSelected = searchState.wholeWord,
                    theme = theme,
                    onClick = onToggleWholeWord
                )

                Spacer(modifier = Modifier.width(6.dp))

                SearchOptionToggle(
                    label = ".*",
                    tooltip = "Regular Expression",
                    isSelected = searchState.isRegex,
                    theme = theme,
                    onClick = onToggleRegex
                )
            }

            // Expandable Replace Row
            AnimatedVisibility(visible = searchState.isReplaceOpen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(36.dp))

                    // Replace Input Field
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.background)
                            .border(1.dp, theme.selectionBackground, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchState.replaceQuery.isEmpty()) {
                            Text(
                                text = "Replace with...",
                                color = theme.gutterText,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        BasicTextField(
                            value = searchState.replaceQuery,
                            onValueChange = onReplaceChanged,
                            textStyle = TextStyle(
                                color = theme.foreground,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(theme.keywordColor),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Replace Current Button
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.currentLineBackground)
                            .clickable(onClick = onReplaceCurrent)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Replace",
                            color = theme.foreground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Replace All Button
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.currentLineBackground)
                            .clickable(onClick = onReplaceAll)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All",
                            color = theme.typeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchOptionToggle(
    label: String,
    tooltip: String,
    isSelected: Boolean,
    theme: EditorTheme,
    onClick: () -> Unit
) {
    val bg = if (isSelected) theme.keywordColor.copy(alpha = 0.25f) else Color.Transparent
    val border = if (isSelected) theme.keywordColor else theme.selectionBackground
    val textColor = if (isSelected) theme.keywordColor else theme.gutterText

    Box(
        modifier = Modifier
            .size(width = 32.dp, height = 26.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace
        )
    }
}
