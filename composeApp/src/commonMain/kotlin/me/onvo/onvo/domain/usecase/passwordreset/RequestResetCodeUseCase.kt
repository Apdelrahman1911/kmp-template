package me.onvo.onvo.domain.usecase.passwordreset

import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.PasswordResetCodeResponse
import me.onvo.onvo.domain.repository.AuthRepository
import me.onvo.onvo.domain.repository.PasswordResetRepository

class RequestResetCodeUseCase(
    private val passwordResetRepository: PasswordResetRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String): State<PasswordResetCodeResponse> {
        if (userId.isBlank()) {
            return State.Error(
                IllegalArgumentException("User ID cannot be empty"),
                "Please enter a valid user ID"
            )
        }

        val token = authRepository.getAuthToken()
            ?: return State.Error(
                IllegalStateException("No auth token"),
                "Please restart the app"
            )

        return passwordResetRepository.requestResetCode(userId.trim(), token)
    }
}