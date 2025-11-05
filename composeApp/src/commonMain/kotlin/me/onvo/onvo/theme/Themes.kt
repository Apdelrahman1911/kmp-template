package me.onvo.onvo.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme Colors
private val LightPrimary = Color(0xFF6200EE)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFBB86FC)
private val LightOnPrimaryContainer = Color(0xFF3700B3)
private val LightSecondary = Color(0xFF03DAC6)
private val LightOnSecondary = Color(0xFF000000)
private val LightSecondaryContainer = Color(0xFF018786)
private val LightOnSecondaryContainer = Color(0xFFFFFFFF)
private val LightTertiary = Color(0xFF03DAC6)
private val LightOnTertiary = Color(0xFF000000)
private val LightError = Color(0xFFB00020)
private val LightOnError = Color(0xFFFFFFFF)
private val LightBackground = Color(0xFFFFFBFE)
private val LightOnBackground = Color(0xFF1C1B1F)
private val LightSurface = Color(0xFFFFFBFE)
private val LightOnSurface = Color(0xFF1C1B1F)
private val LightSurfaceVariant = Color(0xFFE7E0EC)
private val LightOnSurfaceVariant = Color(0xFF49454F)

// Dark Theme Colors
private val DarkPrimary = Color(0xFFBB86FC)
private val DarkOnPrimary = Color(0xFF3700B3)
private val DarkPrimaryContainer = Color(0xFF6200EE)
private val DarkOnPrimaryContainer = Color(0xFFFFFFFF)
private val DarkSecondary = Color(0xFF03DAC6)
private val DarkOnSecondary = Color(0xFF000000)
private val DarkSecondaryContainer = Color(0xFF018786)
private val DarkOnSecondaryContainer = Color(0xFFFFFFFF)
private val DarkTertiary = Color(0xFF03DAC6)
private val DarkOnTertiary = Color(0xFF000000)
private val DarkError = Color(0xFFCF6679)
private val DarkOnError = Color(0xFF000000)
private val DarkBackground = Color(0xFF1C1B1F)
private val DarkOnBackground = Color(0xFFE6E1E5)
private val DarkSurface = Color(0xFF1C1B1F)
private val DarkOnSurface = Color(0xFFE6E1E5)
private val DarkSurfaceVariant = Color(0xFF49454F)
private val DarkOnSurfaceVariant = Color(0xFFCAC4D0)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    error = LightError,
    onError = LightOnError,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    error = DarkError,
    onError = DarkOnError,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}