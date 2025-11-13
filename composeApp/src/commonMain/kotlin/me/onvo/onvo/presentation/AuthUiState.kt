package me.onvo.onvo.presentation

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data class CheckInputSuccess(
        val id: String,
        val type: String,
        val imageUrl: String? = null,
        val fullName: String? = null
    ) : AuthUiState()
    data class LoginSuccess(val userName: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}