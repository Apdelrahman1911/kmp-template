package me.onvo.onvo.data.network

import me.onvo.onvo.domain.model.*

interface PasswordResetApiService {
    suspend fun requestResetCode(userId: String, token: String): PasswordResetCodeResponse
    suspend fun submitResetCode(code: String, token: String): PasswordResetCodeVerifyResponse
    suspend fun changePassword(newPassword: String, token: String): PasswordChangeResponse
}
