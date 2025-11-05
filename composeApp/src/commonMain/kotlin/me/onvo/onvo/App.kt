package me.onvo.onvo


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import me.onvo.onvo.navigation.AppNavigation
import me.onvo.onvo.presentation.ui.SourcesScreen
import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import org.koin.compose.koinInject




@Composable
fun App() {
    MaterialTheme {


        AppNavigation()
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






