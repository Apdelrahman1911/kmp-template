package me.onvo.onvo.navigation


import kotlinx.serialization.Serializable

// Sealed interface for type-safe navigation
sealed interface Screen {
    @Serializable
    data object Login : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Discovery : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data class UserProfile(val userId: String) : Screen

    @Serializable
    data class SettingsDetail(val settingType: String) : Screen

    @Serializable
    data object CreatePost : Screen
}