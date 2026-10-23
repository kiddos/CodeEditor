package app.kiddos.codeeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.kiddos.codeeditor.ui.MainScreen
import app.kiddos.codeeditor.ui.theme.CodeEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeEditorTheme {
                MainScreen()
            }
        }
    }
}