// File: commonMain/kotlin/me/onvo/onvo/domain/repository/PasswordResetRepository.kt
package me.onvo.onvo.domain.repository

import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.*

interface PasswordResetRepository {
    suspend fun requestResetCode(userId: String, token: String): State<PasswordResetCodeResponse>
    suspend fun submitResetCode(code: String, token: String): State<PasswordResetCodeVerifyResponse>
    suspend fun changePassword(newPassword: String, token: String): State<PasswordChangeResponse>
}

