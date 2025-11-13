package me.onvo.onvo.domain.usecase.auth

import me.onvo.onvo.core.util.CryptoUtil
import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.LoginResponse
import me.onvo.onvo.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id: String, password: String): State<LoginResponse> {
        if (id.isBlank() || password.isBlank()) {
            return State.Error(
                IllegalArgumentException("Invalid credentials"),
                "ID and password cannot be empty"
            )
        }

        val token = authRepository.getAuthToken()
            ?: return State.Error(
                IllegalStateException("No auth token"),
                "Please restart the app"
            )

        // Encrypt password with MD5
        val encryptedPassword = CryptoUtil.md5(password)

        return when (val result = authRepository.login(id, encryptedPassword, token)) {
            is State.Success -> {
                if (result.data.isLoged) {
                    // Save user session
                    result.data.data?.id?.let { userId ->
                        result.data.data.username.let { userName ->
                            authRepository.saveUserSession(userId.toString(), userName.toString())
                        }
                    }
                }
                result
            }
            is State.Error -> result
            is State.Loading -> result
        }
    }
}