package me.onvo.onvo.domain.usecase.passwordreset


import me.onvo.onvo.core.util.CryptoUtil
import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.PasswordChangeResponse
import me.onvo.onvo.domain.repository.AuthRepository
import me.onvo.onvo.domain.repository.PasswordResetRepository

class ChangePasswordUseCase(
    private val passwordResetRepository: PasswordResetRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(newPassword: String): State<PasswordChangeResponse> {
        if (newPassword.isBlank() || newPassword.length < 6) {
            return State.Error(
                IllegalArgumentException("Invalid password"),
                "Password must be at least 6 characters"
            )
        }

        val token = authRepository.getAuthToken()
            ?: return State.Error(
                IllegalStateException("No auth token"),
                "Please restart the app"
            )

        // Encrypt password with MD5
        val encryptedPassword = CryptoUtil.md5(newPassword)

        return when (val result = passwordResetRepository.changePassword(encryptedPassword, token)) {
            is State.Success -> {
                // Update user session with new credentials
                result.data.data?.let { user ->
                    authRepository.saveUserSession(
                        userId = user.id.toString(),
                        userName = user.username
                    )
                }
                result
            }
            is State.Error -> result
            is State.Loading -> result
        }
    }
}