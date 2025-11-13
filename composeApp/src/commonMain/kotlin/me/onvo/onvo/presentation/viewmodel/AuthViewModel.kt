package me.onvo.onvo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.AuthState
import me.onvo.onvo.domain.model.AuthUserData
import me.onvo.onvo.domain.usecase.auth.*
import me.onvo.onvo.domain.usecase.profile.GetCurrentUserStatusUseCase
import me.onvo.onvo.presentation.AuthUiState

class AuthViewModel(
    private val getTokenUseCase: GetTokenUseCase,
    private val checkInputUseCase: CheckInputUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserSessionUseCase: GetUserSessionUseCase,
    private val getCurrentUserStatusUseCase: GetCurrentUserStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    // Store current user data for navigation bar
    private val _currentUser = MutableStateFlow<AuthUserData?>(null)
    val currentUser: StateFlow<AuthUserData?> = _currentUser.asStateFlow()

    init {
        initializeAuth()
    }

    private fun initializeAuth() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            // First, check if we have an existing valid session
            val existingSession = getUserSessionUseCase()

            if (existingSession.isAuthenticated && existingSession.authToken != null) {
                // We have a token, verify it with the server
                when (val statusResult = getCurrentUserStatusUseCase()) {
                    is State.Success -> {
                        // Valid session confirmed by server
                        _currentUser.value = statusResult.data
                        _authState.value = AuthState(
                            isAuthenticated = true,
                            userId = statusResult.data.id.toString(),
                            userName = statusResult.data.username,
                            authToken = existingSession.authToken
                        )
                        _isLoggedIn.value = true
                        _uiState.value = AuthUiState.Initial
                        _isInitialized.value = true
                    }
                    is State.Error -> {
                        // Token is invalid or session expired
                        // The repository already cleared the session
                        _currentUser.value = null
                        _isLoggedIn.value = false

                        // Get a new token for login
                        getNewToken()
                    }
                    is State.Loading -> {
                        _uiState.value = AuthUiState.Loading
                    }
                }
            } else {
                // No valid session, get a new token
                getNewToken()
            }
        }
    }

    private suspend fun getNewToken() {
        when (val tokenResult = getTokenUseCase()) {
            is State.Success -> {
                _uiState.value = AuthUiState.Initial
                _isInitialized.value = true
            }
            is State.Error -> {
                _uiState.value = AuthUiState.Error(
                    "Failed to initialize. Please check your internet connection and restart the app."
                )
                _isInitialized.value = true
            }
            is State.Loading -> {
                _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun checkInput(input: String) {
        if (input.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your username, email, or phone number")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            when (val result = checkInputUseCase(input.trim())) {
                is State.Success -> {
                    val data = result.data
                    if (data.isSuccess()) {
                        _uiState.value = AuthUiState.CheckInputSuccess(
                            id = data.id.toString(),
                            type = data.username ?: "user",
                            imageUrl = data.image,
                            fullName = data.fullname
                        )
                    } else {
                        _uiState.value = AuthUiState.Error(
                            data.message ?: "User not found"
                        )
                    }
                }
                is State.Error -> {
                    _uiState.value = AuthUiState.Error(
                        result.message ?: "Failed to verify input. Please try again."
                    )
                }
                is State.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }

    fun login(id: String, password: String) {
        if (password.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your password")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            when (val result = loginUseCase(id, password)) {
                is State.Success -> {
                    val response = result.data
                    if (response.isSuccess() && response.data != null) {
                        val userName = response.data.username

                        // Get fresh user status after login
                        refreshUserStatus()

                        _isLoggedIn.value = true
                        _uiState.value = AuthUiState.LoginSuccess(userName)

                        // Update auth state
                        val session = getUserSessionUseCase()
                        _authState.value = session
                    } else {
                        _uiState.value = AuthUiState.Error(
                            response.message ?: "Login failed. Please check your credentials."
                        )
                    }
                }
                is State.Error -> {
                    _uiState.value = AuthUiState.Error(
                        result.message ?: "Login failed. Please try again."
                    )
                }
                is State.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (logoutUseCase()) {
                is State.Success -> {
                    _isLoggedIn.value = false
                    _authState.value = AuthState()
                    _currentUser.value = null
                    _uiState.value = AuthUiState.Initial

                    // Get a new token for next login
                    getTokenUseCase()
                }
                is State.Error -> {
                    // Still clear local state even if API fails
                    _isLoggedIn.value = false
                    _authState.value = AuthState()
                    _currentUser.value = null
                    _uiState.value = AuthUiState.Initial

                    // Get a new token for next login
                    getTokenUseCase()
                }
                is State.Loading -> {}
            }
        }
    }

    /**
     * Refresh session state and user data
     */
    fun refreshSession() {
        viewModelScope.launch {
            refreshUserStatus()
            val session = getUserSessionUseCase()
            if (session.isAuthenticated) {
                _authState.value = session
                _isLoggedIn.value = true
                _uiState.value = AuthUiState.LoginSuccess(session.userName ?: "User")
            }
        }
    }

    /**
     * Refresh current user status from server
     */
    private suspend fun refreshUserStatus() {
        when (val result = getCurrentUserStatusUseCase()) {
            is State.Success -> {
                _currentUser.value = result.data
            }
            is State.Error -> {
                // If unauthorized, logout
                if (result.message?.contains("expired", ignoreCase = true) == true ||
                    result.message?.contains("unauthorized", ignoreCase = true) == true) {
                    logout()
                }
            }
            is State.Loading -> {}
        }
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Initial
    }

    fun retry() {
        initializeAuth()
    }
}