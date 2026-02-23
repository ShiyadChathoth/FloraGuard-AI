package com.floraguard.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.floraguard.ai.ui.navigation.FloraGuardNavHost
import com.floraguard.ai.ui.theme.FloraGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            FloraGuardTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FloraGuardNavHost(
                        isDarkTheme = darkTheme,
                        onToggleDarkTheme = { enabled -> darkTheme = enabled }
                    )
                }
            }
        }
    }
}
