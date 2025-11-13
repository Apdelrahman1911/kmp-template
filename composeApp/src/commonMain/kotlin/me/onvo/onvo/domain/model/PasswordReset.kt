// File: commonMain/kotlin/me/onvo/onvo/domain/model/PasswordReset.kt
package me.onvo.onvo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequest(
    val id: String
)

@Serializable
data class PasswordResetCodeResponse(
    val alert: Boolean? = null,
    val code: Int? = null,
    val status: String? = null,
    val type: String? = null,
    val message: String? = null,
    // Error fields
    val error: String? = null
) {
    fun isSuccess(): Boolean = status == "success" && error == null
    fun isError(): Boolean = error != null
}

@Serializable
data class PasswordResetCodeSubmit(
    val code: String
)

@Serializable
data class PasswordResetCodeVerifyResponse(
    val status: String? = null,
    val error: String? = null,
    val type: String? = null,
    val message: String? = null
) {
    fun isSuccess(): Boolean = status == "success" && error == null
    fun isError(): Boolean = error != null
}

@Serializable
data class PasswordChangeRequest(
    val password: String
)

@Serializable
data class PasswordChangeResponse(
    val status: String? = null,
    val isLoged: Boolean = false,
    val plus: PlusInfo? = null,
    val data: SessionUser? = null,
    val error: String? = null
) {
    fun isSuccess(): Boolean = isLoged && error == null
    fun isError(): Boolean = error != null
}

sealed class PasswordResetUiState {
    data object Initial : PasswordResetUiState()
    data object Loading : PasswordResetUiState()
    data class CodeSent(val message: String) : PasswordResetUiState()
    data object CodeVerified : PasswordResetUiState()
    data class PasswordChanged(val userName: String) : PasswordResetUiState()
    data class Error(val message: String, val type: ErrorType = ErrorType.GENERAL) : PasswordResetUiState()

    enum class ErrorType {
        GENERAL,
        NO_EMAIL,
        INVALID_CODE,
        USER_NOT_FOUND
    }
}