package me.onvo.onvo.domain.usecase.auth

import me.onvo.onvo.domain.model.AuthState
import me.onvo.onvo.domain.repository.AuthRepository

class GetUserSessionUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): AuthState {
        val (userId, userName) = authRepository.getUserSession()
        val authToken = authRepository.getAuthToken()

        return AuthState(
            isAuthenticated = userId != null && authToken != null,
            userId = userId,
            userName = userName,
            authToken = authToken
        )
    }
}