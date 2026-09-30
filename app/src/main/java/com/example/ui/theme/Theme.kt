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
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = HarvestGold,
    onSecondary = Color(0xFF332000),
    secondaryContainer = Color(0xFF5E4200),
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFFBCAAA4),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFE8F5E9),
    onSurface = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFFCFD8DC)
)

private val LightColorScheme = lightColorScheme(
    primary = FarmGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FarmGreenContainer,
    onPrimaryContainer = FarmOnGreenContainer,
    secondary = HarvestGoldDark,
    onSecondary = Color.White,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = HarvestOnGoldContainer,
    tertiary = EarthSoilBrown,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = Color(0xFF1B241B),
    onSurface = Color(0xFF1B241B),
    onSurfaceVariant = Color(0xFF455A64)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent agricultural theme
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
