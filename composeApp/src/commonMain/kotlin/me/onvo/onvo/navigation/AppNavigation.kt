// FILE: commonMain/kotlin/me/onvo/onvo/navigation/AppNavigation.kt
package me.onvo.onvo.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import me.onvo.onvo.presentation.ui.LoginScreen
import me.onvo.onvo.presentation.ui.PasswordResetScreen
import me.onvo.onvo.presentation.ui.ProfileScreen
import me.onvo.onvo.presentation.ui.SourcesScreen
import me.onvo.onvo.presentation.viewmodel.AuthViewModel
import me.onvo.onvo.presentation.viewmodel.PasswordResetViewModel
import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
import me.onvo.onvo.test.*
import me.onvo.onvo.theme.ThemeViewModel
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = koinInject(),
    passwordResetViewModel: PasswordResetViewModel = koinInject(),
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val sourcesViewModel: SourcesViewModel = koinInject()

    val isUserLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isInitialized by authViewModel.isInitialized.collectAsState()

    val isPasswordResetLoggedIn by passwordResetViewModel.isLoggedIn.collectAsState()

    // Show loading screen while initializing
    if (!isInitialized) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Initializing...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    val startDestination = if (isUserLoggedIn) Screen.Home else Screen.Login

    Scaffold(
        // Remove default content padding - we'll handle it manually
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isUserLoggedIn) {
                BottomNavigationBar(
                    navController = navController,
                    currentUserImage = currentUser?.image
                )
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
        // Apply padding values from Scaffold
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(paddingValues)
        ) {
            composable<Screen.Login> {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    },
                    onForgotPassword = {
                        passwordResetViewModel.reset()
                        navController.navigate(Screen.ForgotPassword)
                    }
                )
            }

            composable<Screen.ForgotPassword> {
                PasswordResetScreen(
                    viewModel = passwordResetViewModel,
                    onBackClick = {
                        passwordResetViewModel.reset()
                        navController.navigateUp()
                    },
                    onPasswordChanged = { isFromSettings ->
                        if (!isFromSettings && isPasswordResetLoggedIn) {
                            authViewModel.refreshSession()
                            navController.navigate(Screen.Home) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            passwordResetViewModel.reset()
                            navController.navigate(Screen.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    isFromSettings = false,
                    prefilledUserId = null
                )
            }

            composable<Screen.ChangePassword> {
                LaunchedEffect(Unit) {
                    passwordResetViewModel.reset()
                }

                PasswordResetScreen(
                    viewModel = passwordResetViewModel,
                    onBackClick = {
                        passwordResetViewModel.reset()
                        navController.navigateUp()
                    },
                    onPasswordChanged = { isFromSettings ->
                        if (isFromSettings) {
                            passwordResetViewModel.reset()
                            navController.navigateUp()
                        }
                    },
                    isFromSettings = true,
                    prefilledUserId = authState.userId
                )
            }

            composable<Screen.Home> {
                SourcesScreen(sourcesViewModel)
            }

            composable<Screen.Search> {
                SearchScreen(
                    onUserClick = { userId ->
                        navController.navigate(Screen.UserProfile(userId))
                    }
                )
            }

            composable<Screen.Discovery> {
                DiscoveryScreen(
                    onUserClick = { userId ->
                        navController.navigate(Screen.UserProfile(userId))
                    }
                )
            }

            composable<Screen.Profile> {
                ProfileScreen()
            }

            composable<Screen.Settings> {
                SettingsScreen(
                    themeViewModel = themeViewModel,
                    onSettingClick = { settingType ->
                        navController.navigate(Screen.SettingsDetail(settingType))
                    },
                    onChangePassword = {
                        navController.navigate(Screen.ChangePassword)
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screen.UserProfile> { backStackEntry ->
                val userProfile: Screen.UserProfile = backStackEntry.toRoute()
                UserProfileScreen(
                    userId = userProfile.userId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<Screen.SettingsDetail> { backStackEntry ->
                val settingsDetail: Screen.SettingsDetail = backStackEntry.toRoute()
                SettingsDetailScreen(
                    settingType = settingsDetail.settingType,
                    onBackClick = { navController.navigateUp() }
                )
            }

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
private fun BottomNavigationBar(
    navController: NavHostController,
    currentUserImage: String?
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home, false),
        BottomNavItem("Search", Icons.Default.Search, Screen.Search, false),
        BottomNavItem("Discovery", Icons.Default.LocationOn, Screen.Discovery, false),
        BottomNavItem("Profile", Icons.Default.Person, Screen.Profile, true),
        BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings, false)
    )

    NavigationBar(
        // Remove extra padding by setting window insets to 0
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.useProfileImage && !currentUserImage.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentUserImage,
                            contentDescription = item.label,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
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
    val screen: Screen,
    val useProfileImage: Boolean = false
)