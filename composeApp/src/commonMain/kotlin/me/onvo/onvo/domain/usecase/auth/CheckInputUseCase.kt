package me.onvo.onvo.domain.usecase.auth

import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.CheckInputResponse
import me.onvo.onvo.domain.repository.AuthRepository

class CheckInputUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(input: String): State<CheckInputResponse> {
        if (input.isBlank()) {
            return State.Error(
                IllegalArgumentException("Input cannot be empty"),
                "Please enter your email, username, or phone number"
            )
        }

        val token = authRepository.getAuthToken()
            ?: return State.Error(
                IllegalStateException("No auth token"),
                "Please restart the app"
            )

        return authRepository.checkInput(input.trim(), token)
    }
}