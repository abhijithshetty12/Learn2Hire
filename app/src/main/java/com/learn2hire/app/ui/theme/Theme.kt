package com.learn2hire.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = BrandBlack,

    secondary = BrandLime,
    onSecondary = BrandBlack,

    tertiary = BrandYellow,
    onTertiary = BrandBlack,

    background = LightBackground,
    onBackground = LightTextPrimary,

    surface = LightSurface,
    onSurface = LightTextPrimary,

    surfaceVariant = LightSurfaceSoft,
    onSurfaceVariant = LightTextSecondary,

    outline = LightBorder,

    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = BrandLime,
    onPrimary = BrandBlack,

    secondary = BrandGreen,
    onSecondary = BrandBlack,

    tertiary = BrandYellow,
    onTertiary = BrandBlack,

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,

    surfaceVariant = DarkSurfaceSoft,
    onSurfaceVariant = DarkTextSecondary,

    outline = DarkBorder,

    error = Color(0xFFFF6B6B)
)

@Composable
fun Learn2HireTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {

    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme =
        if (darkTheme) DarkColors
        else LightColors

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window =
                (view.context as Activity).window

            window.statusBarColor =
                colorScheme.background.toArgb()

            window.navigationBarColor =
                colorScheme.background.toArgb()

            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars =
                !darkTheme

            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightNavigationBars =
                !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}