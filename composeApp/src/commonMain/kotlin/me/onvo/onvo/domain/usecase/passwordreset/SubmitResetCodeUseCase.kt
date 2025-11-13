package me.onvo.onvo.domain.usecase.passwordreset

import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.PasswordResetCodeVerifyResponse
import me.onvo.onvo.domain.repository.AuthRepository
import me.onvo.onvo.domain.repository.PasswordResetRepository

class SubmitResetCodeUseCase(
    private val passwordResetRepository: PasswordResetRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(code: String): State<PasswordResetCodeVerifyResponse> {
        if (code.isBlank() || code.length != 6) {
            return State.Error(
                IllegalArgumentException("Invalid code"),
                "Please enter a valid 6-digit code"
            )
        }

        val token = authRepository.getAuthToken()
            ?: return State.Error(
                IllegalStateException("No auth token"),
                "Please restart the app"
            )

        return passwordResetRepository.submitResetCode(code.trim(), token)
    }
}