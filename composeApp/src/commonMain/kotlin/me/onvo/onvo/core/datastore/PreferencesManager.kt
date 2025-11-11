package me.onvo.onvo.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.onvo.onvo.theme.ThemeMode

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        // Theme preferences
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val THEME_MODE = stringPreferencesKey("theme_mode") // "light", "dark", "system"

        // Drawable/Image preferences
        private val SELECTED_AVATAR = stringPreferencesKey("selected_avatar")
        private val CUSTOM_BACKGROUND = stringPreferencesKey("custom_background")

        // Other preferences
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    // ========== Theme Functions ==========

    /**
     * Save dark mode state
     * @param isDark true for dark mode, false for light mode
     */
    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    /**
     * Get dark mode state as Flow
     * @return Flow<Boolean> that emits true for dark mode, false for light mode
     */
    fun isDarkMode(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_DARK_MODE] ?: false // Default to light mode
        }
    }

    /**
     * Save theme mode (light/dark/system)
     * @param mode "light", "dark", or "system"
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name
        }
    }

    /**
     * Get theme mode as Flow
     * @return Flow<ThemeMode> that emits the current theme mode
     */
    fun getThemeMode(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            val modeName = preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name
            ThemeMode.valueOf(modeName)
        }
    }

    // ========== Drawable/Image Functions ==========

    /**
     * Save selected avatar path or identifier
     * @param avatarPath path to the drawable resource or file
     */
    suspend fun setSelectedAvatar(avatarPath: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_AVATAR] = avatarPath
        }
    }

    /**
     * Get selected avatar as Flow
     * @return Flow<String?> that emits the avatar path
     */
    fun getSelectedAvatar(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[SELECTED_AVATAR]
        }
    }

    /**
     * Save custom background image path
     * @param backgroundPath path to the background image
     */
    suspend fun setCustomBackground(backgroundPath: String) {
        dataStore.edit { preferences ->
            preferences[CUSTOM_BACKGROUND] = backgroundPath
        }
    }

    /**
     * Get custom background as Flow
     * @return Flow<String?> that emits the background path
     */
    fun getCustomBackground(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[CUSTOM_BACKGROUND]
        }
    }

    // ========== Other Utility Functions ==========

    /**
     * Check if this is the first app launch
     */
    fun isFirstLaunch(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }
    }

    /**
     * Mark that the app has been launched
     */
    suspend fun setFirstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }

    /**
     * Clear all preferences (useful for logout)
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}