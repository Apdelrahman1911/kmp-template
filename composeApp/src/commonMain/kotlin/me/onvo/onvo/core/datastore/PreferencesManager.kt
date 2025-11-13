// File: commonMain/kotlin/me/onvo/onvo/core/datastore/PreferencesManager.kt
// Complete updated PreferencesManager with auth methods
package me.onvo.onvo.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.onvo.onvo.theme.ThemeMode

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        // Theme preferences
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val THEME_MODE = stringPreferencesKey("theme_mode")

        // Drawable/Image preferences
        private val SELECTED_AVATAR = stringPreferencesKey("selected_avatar")
        private val CUSTOM_BACKGROUND = stringPreferencesKey("custom_background")

        // Auth preferences
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")

        // Other preferences
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    // ========== Theme Functions ==========

    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    fun isDarkMode(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name
        }
    }

    fun getThemeMode(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            val modeName = preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name
            ThemeMode.valueOf(modeName)
        }
    }

    // ========== Drawable/Image Functions ==========

    suspend fun setSelectedAvatar(avatarPath: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_AVATAR] = avatarPath
        }
    }

    fun getSelectedAvatar(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[SELECTED_AVATAR]
        }
    }

    suspend fun setCustomBackground(backgroundPath: String) {
        dataStore.edit { preferences ->
            preferences[CUSTOM_BACKGROUND] = backgroundPath
        }
    }

    fun getCustomBackground(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[CUSTOM_BACKGROUND]
        }
    }

    // ========== Auth Functions ==========

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    suspend fun getAuthToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN]
        }.first()
    }

    suspend fun clearAuthToken() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
        }
    }

    suspend fun saveUserSession(userId: String, userName: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = userName
        }
    }

    suspend fun getUserSession(): Pair<String?, String?> {
        return dataStore.data.map { preferences ->
            Pair(preferences[USER_ID], preferences[USER_NAME])
        }.first()
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_NAME)
        }
    }

    // ========== Other Utility Functions ==========

    fun isFirstLaunch(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }
    }

    suspend fun setFirstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}