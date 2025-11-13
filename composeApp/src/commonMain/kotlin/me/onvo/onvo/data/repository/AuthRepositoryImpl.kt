// File: commonMain/kotlin/me/onvo/onvo/data/repository/AuthRepositoryImpl.kt
package me.onvo.onvo.data.repository

import me.onvo.onvo.core.datastore.PreferencesManager
import me.onvo.onvo.data.network.AuthApiService
import me.onvo.onvo.domain.repository.AuthRepository
import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.CheckInputResponse
import me.onvo.onvo.domain.model.DeviceInfo
import me.onvo.onvo.domain.model.LoginRequest
import me.onvo.onvo.domain.model.LoginResponse
import me.onvo.onvo.domain.model.TokenRequest
import me.onvo.onvo.domain.model.TokenResponse

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun getToken(deviceInfo: DeviceInfo): State<TokenResponse> {
        return try {
            val response = authApiService.getToken(TokenRequest(deviceInfo))
            State.Success(response)
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Failed to get token")
        }
    }

    override suspend fun checkInput(input: String, token: String): State<CheckInputResponse> {
        return try {
            val response = authApiService.checkInput(input, token)

            // Check if response contains an error
            if (response.isError()) {
                State.Error(
                    exception = Exception(response.error),
                    message = response.message ?: "User not found"
                )
            } else if (response.isSuccess()) {
                State.Success(response)
            } else {
                State.Error(
                    exception = Exception("Invalid response"),
                    message = "Received invalid response from server"
                )
            }
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Failed to check input")
        }
    }

    override suspend fun login(
        id: String,
        password: String,
        token: String
    ): State<LoginResponse> {
        return try {
            val request = LoginRequest(id, password)
            val response = authApiService.login(request, token)

            // Check if response contains an error
            if (response.isError()) {
                State.Error(
                    exception = Exception(response.error),
                    message = response.message ?: "Login failed"
                )
            } else if (response.isSuccess()) {
                State.Success(response)
            } else {
                State.Error(
                    exception = Exception("Invalid credentials"),
                    message = "Login failed. Please check your credentials."
                )
            }
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Login failed")
        }
    }

    override suspend fun logout(token: String): State<Unit> {
        return try {
            authApiService.logout(token)
            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Logout failed")
        }
    }

    // Local storage methods
    override suspend fun saveAuthToken(token: String) {
        preferencesManager.saveAuthToken(token)
    }

    override suspend fun getAuthToken(): String? {
        return preferencesManager.getAuthToken()
    }

    override suspend fun clearAuthToken() {
        preferencesManager.clearAuthToken()
    }

    override suspend fun saveUserSession(userId: String, userName: String) {
        preferencesManager.saveUserSession(userId, userName)
    }

    override suspend fun getUserSession(): Pair<String?, String?> {
        return preferencesManager.getUserSession()
    }

    override suspend fun clearUserSession() {
        preferencesManager.clearUserSession()
    }
}