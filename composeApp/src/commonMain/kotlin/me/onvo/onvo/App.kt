package me.onvo.onvo


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import me.onvo.onvo.navigation.AppNavigation
import me.onvo.onvo.presentation.ui.SourcesScreen
import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import org.koin.compose.koinInject
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import me.onvo.onvo.theme.AppTheme
import me.onvo.onvo.theme.ThemeManager
import me.onvo.onvo.theme.ThemeMode
import me.onvo.onvo.theme.ThemeViewModel
import me.onvo.onvo.theme.splash_screen.SplashScreen
import me.onvo.onvo.theme.splash_screen.shouldShowSplash


@Composable
fun App() {


    var showSplash by remember { mutableStateOf(shouldShowSplash) }


    val themeViewModel: ThemeViewModel = koinInject()
    val themeMode by themeViewModel.themeMode.collectAsState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val systemInDarkTheme = isSystemInDarkTheme()

    // Determine if dark theme should be used
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        if (showSplash) {
            SplashScreen(
                onSplashFinished = { showSplash = false }
            )
        } else {
            AppNavigation(themeViewModel = themeViewModel)
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






