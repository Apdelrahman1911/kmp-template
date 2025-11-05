package me.onvo.onvo


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import me.onvo.onvo.navigation.AppNavigation
import me.onvo.onvo.presentation.ui.SourcesScreen
import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import org.koin.compose.koinInject
import androidx.compose.foundation.isSystemInDarkTheme
import me.onvo.onvo.theme.AppTheme
import me.onvo.onvo.theme.ThemeManager
import me.onvo.onvo.theme.ThemeMode
import me.onvo.onvo.theme.splash_screen.SplashScreen


@Composable
fun App() {
    val themeManager: ThemeManager = koinInject()
    val themeMode by themeManager.themeMode.collectAsState()
    val isSystemInDark = isSystemInDarkTheme()

    var showSplash by remember { mutableStateOf(true) }

    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDark
    }

    AppTheme(darkTheme = darkTheme) {
        if (showSplash) {
            SplashScreen(
                onSplashFinished = { showSplash = false }
            )
        } else {
            AppNavigation()
        }
    }
}
//@Composable
//fun App() {
//    MaterialTheme {
//        val viewModel: SourcesViewModel = koinInject()
//        SourcesScreen(viewModel = viewModel)
//    }
//}
//






