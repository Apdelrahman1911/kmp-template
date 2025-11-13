package me.onvo.onvo.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.ProfileUiState
import me.onvo.onvo.domain.model.AuthStatusUiState
import me.onvo.onvo.domain.usecase.profile.GetCurrentUserStatusUseCase
import me.onvo.onvo.domain.usecase.profile.GetUserProfileUseCase

class ProfileViewModel(
    private val getCurrentUserStatusUseCase: GetCurrentUserStatusUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _currentUserState = MutableStateFlow<AuthStatusUiState>(AuthStatusUiState.Loading)
    val currentUserState: StateFlow<AuthStatusUiState> = _currentUserState.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadCurrentUserStatus()
    }

    fun loadCurrentUserStatus() {
        viewModelScope.launch {
            _currentUserState.value = AuthStatusUiState.Loading

            when (val result = getCurrentUserStatusUseCase()) {
                is State.Success -> {
                    _currentUserState.value = AuthStatusUiState.Success(result.data)
                }
                is State.Error -> {
                    _currentUserState.value = AuthStatusUiState.Error(
                        result.message ?: "Failed to load user status"
                    )
                }
                is State.Loading -> {
                    _currentUserState.value = AuthStatusUiState.Loading
                }
            }
        }
    }

    fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading

            when (val result = getUserProfileUseCase(userId)) {
                is State.Success -> {
                    _profileState.value = ProfileUiState.Success(result.data)
                }
                is State.Error -> {
                    _profileState.value = ProfileUiState.Error(
                        result.message ?: "Failed to load profile"
                    )
                }
                is State.Loading -> {
                    _profileState.value = ProfileUiState.Loading
                }
            }
        }
    }

    fun refreshProfile(userId: Int) {
        viewModelScope.launch {
            _isRefreshing.value = true

            when (val result = getUserProfileUseCase(userId)) {
                is State.Success -> {
                    _profileState.value = ProfileUiState.Success(result.data)
                }
                is State.Error -> {
                    _profileState.value = ProfileUiState.Error(
                        result.message ?: "Failed to refresh profile"
                    )
                }
                is State.Loading -> {}
            }

            _isRefreshing.value = false
        }
    }

    fun retry(userId: Int? = null) {
        if (userId != null) {
            loadUserProfile(userId)
        } else {
            loadCurrentUserStatus()
        }
    }
}