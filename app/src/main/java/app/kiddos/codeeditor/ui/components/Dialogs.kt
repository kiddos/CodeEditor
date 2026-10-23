package app.kiddos.codeeditor.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kiddos.codeeditor.data.EditorTheme
import app.kiddos.codeeditor.data.EncodingHelper
import app.kiddos.codeeditor.data.ProgrammingLanguage
import java.nio.charset.Charset

@Composable
fun EncodingDialog(
    currentEncoding: Charset,
    theme: EditorTheme,
    onDismiss: () -> Unit,
    onReloadWithEncoding: (Charset) -> Unit,
    onSetEncoding: (Charset) -> Unit
) {
    var selectedCharset by remember { mutableStateOf(currentEncoding) }
    val encodings = EncodingHelper.supportedEncodings

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = theme.background,
        title = {
            Text(
                text = "File Encoding",
                color = theme.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select character encoding to reload or save this file:",
                    color = theme.gutterText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    items(encodings) { option ->
                        val isSelected = option.charset.name() == selectedCharset.name()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { selectedCharset = option.charset }
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedCharset = option.charset },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = theme.keywordColor,
                                    unselectedColor = theme.gutterText
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = option.displayName,
                                color = if (isSelected) theme.foreground else theme.gutterText,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row {
                TextButton(
                    onClick = {
                        onReloadWithEncoding(selectedCharset)
                        onDismiss()
                    }
                ) {
                    Text("Reload", color = theme.typeColor, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        onSetEncoding(selectedCharset)
                        onDismiss()
                    }
                ) {
                    Text("Set", color = theme.keywordColor, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = theme.gutterText)
            }
        }
    )
}

@Composable
fun ThemeDialog(
    currentTheme: EditorTheme,
    onThemeSelected: (EditorTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = currentTheme.background,
        title = {
            Text(
                text = "Color Theme",
                color = currentTheme.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                items(EditorTheme.allThemes) { themeItem ->
                    val isSelected = themeItem.id == currentTheme.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(themeItem.background)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) themeItem.keywordColor else themeItem.gutterText.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onThemeSelected(themeItem)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = themeItem.name,
                                color = themeItem.foreground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (themeItem.isDark) "Dark" else "Light",
                                color = themeItem.gutterText,
                                fontSize = 11.sp
                            )
                        }

                        // Theme color preview dots
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ColorDot(themeItem.keywordColor)
                            Spacer(modifier = Modifier.width(4.dp))
                            ColorDot(themeItem.typeColor)
                            Spacer(modifier = Modifier.width(4.dp))
                            ColorDot(themeItem.stringColor)
                            Spacer(modifier = Modifier.width(4.dp))
                            ColorDot(themeItem.functionColor)

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = themeItem.keywordColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = currentTheme.keywordColor)
            }
        }
    )
}

@Composable
private fun ColorDot(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun LanguageDialog(
    currentLanguage: ProgrammingLanguage,
    theme: EditorTheme,
    onLanguageSelected: (ProgrammingLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = theme.background,
        title = {
            Text(
                text = "Select Language",
                color = theme.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                items(ProgrammingLanguage.entries) { lang ->
                    val isSelected = lang == currentLanguage
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onLanguageSelected(lang)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.currentLineBackground)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = lang.badge,
                                    color = theme.keywordColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = lang.displayName,
                                color = if (isSelected) theme.foreground else theme.gutterText,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = theme.keywordColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = theme.gutterText)
            }
        }
    )
}

@Composable
fun NewFileDialog(
    theme: EditorTheme,
    onDismiss: () -> Unit,
    onEmptyFile: () -> Unit,
    onSampleFile: (ProgrammingLanguage) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = theme.background,
        title = {
            Text(
                text = "New File",
                color = theme.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Empty File Option
                NewFileOptionItem(
                    title = "Blank File",
                    subtitle = "Create an empty plain text file",
                    badge = "TXT",
                    theme = theme,
                    onClick = {
                        onEmptyFile()
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // C++ Sample
                NewFileOptionItem(
                    title = "C++ Program",
                    subtitle = "main.cpp (Stack template, vector, lambda)",
                    badge = "C++",
                    theme = theme,
                    onClick = {
                        onSampleFile(ProgrammingLanguage.CPP)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Python Sample
                NewFileOptionItem(
                    title = "Python Script",
                    subtitle = "script.py (asyncio, dataclass, typing)",
                    badge = "Py",
                    theme = theme,
                    onClick = {
                        onSampleFile(ProgrammingLanguage.PYTHON)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Rust Sample
                NewFileOptionItem(
                    title = "Rust Module",
                    subtitle = "main.rs (TaskManager, enum match, HashMap)",
                    badge = "Rs",
                    theme = theme,
                    onClick = {
                        onSampleFile(ProgrammingLanguage.RUST)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Java Sample
                NewFileOptionItem(
                    title = "Java Class",
                    subtitle = "Main.java (record, streams, lambdas)",
                    badge = "Jv",
                    theme = theme,
                    onClick = {
                        onSampleFile(ProgrammingLanguage.JAVA)
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = theme.gutterText)
            }
        }
    )
}

@Composable
private fun NewFileOptionItem(
    title: String,
    subtitle: String,
    badge: String,
    theme: EditorTheme,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(theme.gutterBackground)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(theme.currentLineBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badge,
                color = theme.keywordColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                color = theme.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = theme.gutterText,
                fontSize = 11.sp
            )
        }
    }
}
