package me.onvo.onvo.domain.repository

import me.onvo.onvo.domain.model.CheckInputResponse
import me.onvo.onvo.domain.model.DeviceInfo
import me.onvo.onvo.domain.model.LoginResponse
import me.onvo.onvo.domain.model.TokenResponse
import me.onvo.onvo.core.util.State

interface AuthRepository {
    suspend fun getToken(deviceInfo: DeviceInfo): State<TokenResponse>
    suspend fun checkInput(input: String, token: String): State<CheckInputResponse>
    suspend fun login(id: String, password: String, token: String): State<LoginResponse>
    suspend fun logout(token: String): State<Unit>

    // Local storage methods
    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun clearAuthToken()

    suspend fun saveUserSession(userId: String, userName: String)
    suspend fun getUserSession(): Pair<String?, String?>
    suspend fun clearUserSession()
}