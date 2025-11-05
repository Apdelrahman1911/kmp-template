package me.onvo.onvo.navigation


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import me.onvo.onvo.presentation.ui.SourcesScreen
import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import me.onvo.onvo.test.CreatePostScreen
import me.onvo.onvo.test.DiscoveryScreen
import me.onvo.onvo.test.HomeScreen
import me.onvo.onvo.test.LoginScreen
import me.onvo.onvo.test.ProfileScreen
import me.onvo.onvo.test.SearchScreen
import me.onvo.onvo.test.SettingsDetailScreen
import me.onvo.onvo.test.SettingsScreen
import me.onvo.onvo.test.UserProfileScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
        val sourcesViewModel: SourcesViewModel = koinInject()

//    val authViewModel: AuthViewModel = koinInject()
    val isUserLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val startDestination = remember(isUserLoggedIn) {if (isUserLoggedIn) Screen.Home else Screen.Login}

    Scaffold(
        bottomBar = {
            println("kdjfksdfsdsdaasd ============== $isUserLoggedIn")
            if (isUserLoggedIn) {
                BottomNavigationBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (isUserLoggedIn && shouldShowFab(navController)) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreatePost) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Post")
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(paddingValues)
        ) {
            // Login Screen
            composable<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home) {

                            popUpTo(Screen.Login) { inclusive = true }
                            authViewModel.login()
                        }
                    }
                )
            }

            // Home Screen
            composable<Screen.Home> {

                SourcesScreen(
                    sourcesViewModel
                )
//                HomeScreen(
//                    onUserClick = { userId ->
//                        navController.navigate(Screen.UserProfile(userId))
//                    }
//                )
            }

            // Search Screen
            composable<Screen.Search> {
                SearchScreen(
                    onUserClick = { userId ->
                        navController.navigate(Screen.UserProfile(userId))
                    }
                )
            }

            // Discovery Screen
            composable<Screen.Discovery> {
                DiscoveryScreen(
                    onUserClick = { userId ->
                        navController.navigate(Screen.UserProfile(userId))
                    }
                )
            }

            // Profile Screen (Current User)
            composable<Screen.Profile> {
                ProfileScreen()
            }

            // Settings Screen
            composable<Screen.Settings> {
                SettingsScreen(
                    onSettingClick = { settingType ->
                        navController.navigate(Screen.SettingsDetail(settingType))
                    },
                    onLogout = {
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // User Profile Screen (Other Users)
            composable<Screen.UserProfile> { backStackEntry ->
                val userProfile: Screen.UserProfile = backStackEntry.toRoute()
                UserProfileScreen(
                    userId = userProfile.userId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            // Settings Detail Screen
            composable<Screen.SettingsDetail> { backStackEntry ->
                val settingsDetail: Screen.SettingsDetail = backStackEntry.toRoute()
                SettingsDetailScreen(
                    settingType = settingsDetail.settingType,
                    onBackClick = { navController.navigateUp() }
                )
            }

            // Create Post Screen
            composable<Screen.CreatePost> {
                CreatePostScreen(
                    onPostCreated = { navController.navigateUp() },
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}

@Composable
private fun shouldShowFab(navController: NavHostController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return currentRoute?.contains("Home") == true
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home),
        BottomNavItem("Search", Icons.Default.Search, Screen.Search),
        BottomNavItem("Discovery", Icons.Default.LocationOn, Screen.Discovery),
        BottomNavItem("Profile", Icons.Default.Person, Screen.Profile),
        BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute?.contains(item.screen::class.simpleName ?: "") == true,
                onClick = {
                    navController.navigate(item.screen) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: Screen
)