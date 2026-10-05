package com.learn2hire.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.learn2hire.app.ui.Learn2HireApp
import com.learn2hire.app.ui.theme.Learn2HireTheme
import com.learn2hire.app.ui.theme.ThemeMode
import com.learn2hire.app.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {

    private lateinit var themePreferences:
            ThemePreferences

    private var themeMode by
    mutableStateOf(ThemeMode.SYSTEM)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        themePreferences =
            ThemePreferences(this)

        themeMode =
            themePreferences.getThemeMode()

        setContent {

            Learn2HireTheme(
                themeMode = themeMode
            ) {

                Learn2HireApp(
                    themeMode = themeMode,

                    onThemeModeChange = { newMode ->

                        themeMode = newMode

                        themePreferences
                            .saveThemeMode(newMode)
                    }
                )
            }
        }
    }
}