package me.onvo.onvo.theme


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.onvo.onvo.core.datastore.PreferencesManager

class ThemeViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        loadThemePreferences()
    }

    private fun loadThemePreferences() {
        viewModelScope.launch {
            preferencesManager.getThemeMode().collect { mode ->
                _themeMode.value = mode
            }
        }

        viewModelScope.launch {
            preferencesManager.isDarkMode().collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newDarkMode = !_isDarkMode.value
            preferencesManager.setDarkMode(newDarkMode)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }
}