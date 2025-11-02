package com.example.babel.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Dark theme color scheme — luxurious and moody
private val DarkColorScheme = darkColorScheme(
    primary = DeepPlum,                 // Main brand color
    onPrimary = PaleWhite,              // Text/icons on primary
    secondary = Amethyst,               // Accent hue
    onSecondary = PaleWhite,
    tertiary = MysticGold,              // Gold highlight
    onTertiary = MidnightBlue,
    background = MidnightBlue,          // Main background
    onBackground = PaleWhite,           // Readable foreground text
    surface = DeepPlum.copy(alpha = 0.6f), // Slightly lifted surfaces
    onSurface = PaleWhite,
    surfaceVariant = DeepPlum.copy(alpha = 0.4f),
    onSurfaceVariant = PaleWhite
)

// Light theme color scheme — elegant but soft
private val LightColorScheme = lightColorScheme(
    primary = Amethyst,                 // Brand purple
    onPrimary = PaleWhite,
    secondary = DeepPlum,               // Deeper purple for contrast
    onSecondary = PaleWhite,
    tertiary = MysticGold,              // Gold accent
    onTertiary = MidnightBlue,
    background = PaleWhite,             // Clean, subtle base
    onBackground = MidnightBlue,        // Deep readable text
    surface = SilverAccent.copy(alpha = 0.3f),
    onSurface = MidnightBlue,
    surfaceVariant = Amethyst.copy(alpha = 0.2f),
    onSurfaceVariant = PaleWhite
)

@Composable
fun BabelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // 👈 follows system setting
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
