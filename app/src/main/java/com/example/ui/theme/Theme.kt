package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandTealLight,
    onPrimary = Slate900,
    primaryContainer = BrandTealDark,
    onPrimaryContainer = BrandTealContainer,
    secondary = BrandAmberAccent,
    onSecondary = Slate900,
    secondaryContainer = BrandAmberOnContainer,
    onSecondaryContainer = BrandAmberContainer,
    tertiary = BrandBlueContainer,
    background = Slate900,
    surface = Slate800,
    surfaceVariant = Slate700,
    onBackground = Slate50,
    onSurface = Slate50,
    onSurfaceVariant = Slate300,
    outline = Slate600
)

private val LightColorScheme = lightColorScheme(
    primary = BrandTealPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandTealContainer,
    onPrimaryContainer = BrandTealOnContainer,
    secondary = BrandAmberAccent,
    onSecondary = Slate900,
    secondaryContainer = BrandAmberContainer,
    onSecondaryContainer = BrandAmberOnContainer,
    tertiary = BrandBlue,
    background = Slate50,
    surface = Color.White,
    surfaceVariant = Slate100,
    onBackground = Slate900,
    onSurface = Slate900,
    onSurfaceVariant = Slate600,
    outline = Slate300
)

@Composable
fun HomeHelpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent HomeHelp brand colors by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
