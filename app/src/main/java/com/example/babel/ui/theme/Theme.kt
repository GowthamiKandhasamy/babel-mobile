package com.example.babel.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
// REMOVE the incorrect import if it exists: import kotlin.text.Typography

// The import for your custom Typography object might be added automatically by the IDE
// or you can add it manually:
import com.example.babel.ui.theme.Typography // Make sure this line is present

private val DarkColorScheme = darkColorScheme(
    primary = Amethyst,
    secondary = DeepPlum,
    tertiary = SilverAccent,
    background = MidnightBlue,
    surface = DeepPlum,
    onPrimary = SilverAccent,
    onSecondary = SilverAccent,
    onTertiary = MidnightBlue,
    onBackground = PaleWhite,
    onSurface = PaleWhite
)

private val LightColorScheme = lightColorScheme(
    primary = DeepPlum,
    secondary = Amethyst,
    tertiary = MysticGold,
    background = SilverAccent,
    surface = SilverAccent,
    onPrimary = SilverAccent,
    onSecondary = MidnightBlue,
    onTertiary = MidnightBlue,
    onBackground = MidnightBlue,
    onSurface = MidnightBlue
)

@Composable
fun BabelTheme(
    darkTheme: Boolean = true,
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
        // This now correctly refers to the Typography object from your Type.kt file
        typography = Typography,
        content = content
    )
}
