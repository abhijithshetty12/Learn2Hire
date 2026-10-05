package com.learn2hire.app.ui.theme

import android.content.Context

class ThemePreferences(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "learn2hire_preferences",
            Context.MODE_PRIVATE
        )

    fun getThemeMode(): ThemeMode {

        val value =
            preferences.getString(
                "theme_mode",
                ThemeMode.SYSTEM.name
            )

        return try {
            ThemeMode.valueOf(
                value ?: ThemeMode.SYSTEM.name
            )
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun saveThemeMode(
        themeMode: ThemeMode
    ) {
        preferences
            .edit()
            .putString(
                "theme_mode",
                themeMode.name
            )
            .apply()
    }
}