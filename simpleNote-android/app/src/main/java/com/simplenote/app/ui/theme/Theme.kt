package com.simplenote.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = CardSurface,
    primaryContainer = TerracottaLight,
    onPrimaryContainer = TerracottaDark,
    background = PaperBackground,
    onBackground = CharcoalInk,
    surface = CardSurface,
    onSurface = CharcoalInk,
    surfaceVariant = PaperBackground,
    onSurfaceVariant = StoneMuted,
    outline = BorderBeige
)

@Composable
fun SimpleNoteTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = TerracottaPrimary.toArgb()
            window.navigationBarColor = PaperBackground.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
