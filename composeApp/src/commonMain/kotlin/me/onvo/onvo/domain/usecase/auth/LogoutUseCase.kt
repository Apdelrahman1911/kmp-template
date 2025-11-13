package me.onvo.onvo.domain.usecase.auth

import me.onvo.onvo.domain.repository.AuthRepository
import me.onvo.onvo.core.util.State


class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): State<Unit> {
        val token = authRepository.getAuthToken()
            ?: return State.Error(
                IllegalStateException("No auth token"),
                "Already logged out"
            )

        return when (val result = authRepository.logout(token)) {
            is State.Success -> {
                // Clear local session
                authRepository.clearUserSession()
                result
            }
            is State.Error -> {
                // Clear local session even if API call fails
                authRepository.clearUserSession()
                result
            }
            is State.Loading -> result
        }
    }
}