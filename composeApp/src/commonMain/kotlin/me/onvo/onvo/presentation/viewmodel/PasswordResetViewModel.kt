// File: commonMain/kotlin/me/onvo/onvo/presentation/viewmodel/PasswordResetViewModel.kt
package me.onvo.onvo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.PasswordResetUiState
import me.onvo.onvo.domain.repository.AuthRepository
import me.onvo.onvo.domain.usecase.auth.CheckInputUseCase
import me.onvo.onvo.domain.usecase.passwordreset.*

// Enhanced UI State with explicit step tracking
sealed class PasswordResetStep {
    data object EnterUserId : PasswordResetStep()
    data class EnterCode(val userId: String, val message: String?) : PasswordResetStep()
    data class EnterNewPassword(val userId: String) : PasswordResetStep()
    data class Success(val userName: String, val isFromSettings: Boolean) : PasswordResetStep()
}

data class PasswordResetState(
    val currentStep: PasswordResetStep = PasswordResetStep.EnterUserId,
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorType: PasswordResetUiState.ErrorType = PasswordResetUiState.ErrorType.GENERAL,
    val isFromSettings: Boolean = false
)

class PasswordResetViewModel(
    private val checkInputUseCase: CheckInputUseCase,
    private val requestResetCodeUseCase: RequestResetCodeUseCase,
    private val submitResetCodeUseCase: SubmitResetCodeUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PasswordResetState())
    val state: StateFlow<PasswordResetState> = _state.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    /**
     * Initialize from settings with known user ID
     * User is already logged in, just changing password
     */
    fun initializeFromSettings(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                currentStep = PasswordResetStep.EnterCode(userId, null),
                isLoading = true,
                error = null,
                isFromSettings = true  // Mark as settings flow
            )

            when (val result = requestResetCodeUseCase(userId)) {
                is State.Success -> {
                    val response = result.data
                    if (response.error == "email_not_found") {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = response.message ?: "This account does not have a recovery email.",
                            errorType = PasswordResetUiState.ErrorType.NO_EMAIL
                        )
                    } else if (response.isSuccess()) {
                        _state.value = _state.value.copy(
                            currentStep = PasswordResetStep.EnterCode(
                                userId = userId,
                                message = response.message
                            ),
                            isLoading = false,
                            error = null
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = response.message ?: "Failed to send reset code"
                        )
                    }
                }
                is State.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to send reset code. Please try again."
                    )
                }
                is State.Loading -> {}
            }
        }
    }

    /**
     * Step 1: Check user input and request code
     * This is for forgot password flow (user not logged in)
     */
    fun requestCodeByInput(input: String) {
        if (input.isBlank()) {
            _state.value = _state.value.copy(
                error = "Please enter your username, email, or phone number",
                errorType = PasswordResetUiState.ErrorType.GENERAL
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                isFromSettings = false  // Mark as forgot password flow
            )

            // First, check the input to get user ID
            when (val checkResult = checkInputUseCase(input)) {
                is State.Success -> {
                    val userId = checkResult.data.id.toString()
                    // Now request the reset code
                    requestCode(userId)
                }
                is State.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = checkResult.message ?: "User not found. Please check your input.",
                        errorType = PasswordResetUiState.ErrorType.USER_NOT_FOUND
                    )
                }
                is State.Loading -> {}
            }
        }
    }

    /**
     * Request reset code with user ID
     */
    private suspend fun requestCode(userId: String) {
        when (val result = requestResetCodeUseCase(userId)) {
            is State.Success -> {
                val response = result.data
                when {
                    response.error == "email_not_found" -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = response.message ?: "This account does not have a recovery email.",
                            errorType = PasswordResetUiState.ErrorType.NO_EMAIL
                        )
                    }
                    response.isSuccess() -> {
                        // Successfully sent code - move to next step
                        _state.value = _state.value.copy(
                            currentStep = PasswordResetStep.EnterCode(
                                userId = userId,
                                message = response.message
                            ),
                            isLoading = false,
                            error = null
                        )
                    }
                    else -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = response.message ?: "Failed to send reset code"
                        )
                    }
                }
            }
            is State.Error -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.message ?: "Failed to send reset code. Please try again."
                )
            }
            is State.Loading -> {}
        }
    }

    /**
     * Step 2: Submit verification code
     */
    fun submitCode(code: String) {
        val currentStep = _state.value.currentStep
        if (currentStep !is PasswordResetStep.EnterCode) return

        if (code.length != 6) {
            _state.value = _state.value.copy(
                error = "Please enter a valid 6-digit code",
                errorType = PasswordResetUiState.ErrorType.INVALID_CODE
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = submitResetCodeUseCase(code)) {
                is State.Success -> {
                    // Code verified - move to password entry
                    _state.value = _state.value.copy(
                        currentStep = PasswordResetStep.EnterNewPassword(currentStep.userId),
                        isLoading = false,
                        error = null
                    )
                }
                is State.Error -> {
                    // Stay on code step but show error
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Invalid code. Please try again.",
                        errorType = PasswordResetUiState.ErrorType.INVALID_CODE
                    )
                }
                is State.Loading -> {}
            }
        }
    }

    /**
     * Step 3: Change password
     * Behavior differs based on isFromSettings:
     * - Forgot Password: Auto-login after success
     * - Change Password: Stay logged in
     */
    fun changePassword(newPassword: String, confirmPassword: String) {
        when {
            newPassword.isBlank() -> {
                _state.value = _state.value.copy(
                    error = "Please enter a new password"
                )
                return
            }
            newPassword.length < 6 -> {
                _state.value = _state.value.copy(
                    error = "Password must be at least 6 characters"
                )
                return
            }
            newPassword != confirmPassword -> {
                _state.value = _state.value.copy(
                    error = "Passwords do not match"
                )
                return
            }
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = changePasswordUseCase(newPassword)) {
                is State.Success -> {
                    val userName = result.data.data?.username ?: "User"
                    val isFromSettings = _state.value.isFromSettings

                    // If from forgot password flow, the user is now logged in
                    if (!isFromSettings) {
                        _isLoggedIn.value = true
                    }

                    _state.value = _state.value.copy(
                        currentStep = PasswordResetStep.Success(userName, isFromSettings),
                        isLoading = false,
                        error = null
                    )
                }
                is State.Error -> {
                    // Stay on password step but show error
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to change password. Please try again."
                    )
                }
                is State.Loading -> {}
            }
        }
    }

    /**
     * Resend code (from code entry step)
     */
    fun resendCode() {
        val currentStep = _state.value.currentStep
        if (currentStep !is PasswordResetStep.EnterCode) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            requestCode(currentStep.userId)
        }
    }

    /**
     * Go back to previous step
     */
    fun goBack() {
        val currentStep = _state.value.currentStep
        _state.value = when (currentStep) {
            is PasswordResetStep.EnterCode -> {
                _state.value.copy(
                    currentStep = PasswordResetStep.EnterUserId,
                    error = null
                )
            }
            is PasswordResetStep.EnterNewPassword -> {
                _state.value.copy(
                    currentStep = PasswordResetStep.EnterCode(currentStep.userId, null),
                    error = null
                )
            }
            else -> _state.value
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    /**
     * Reset to initial state
     */
    fun reset() {
        _state.value = PasswordResetState()
        _isLoggedIn.value = false
    }
}