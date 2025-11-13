package me.onvo.onvo.data.repository


import me.onvo.onvo.core.util.State
import me.onvo.onvo.data.network.PasswordResetApiService
import me.onvo.onvo.domain.model.*
import me.onvo.onvo.domain.repository.PasswordResetRepository

class PasswordResetRepositoryImpl(
    private val passwordResetApiService: PasswordResetApiService
) : PasswordResetRepository {

    override suspend fun requestResetCode(
        userId: String,
        token: String
    ): State<PasswordResetCodeResponse> {
        return try {
            val response = passwordResetApiService.requestResetCode(userId, token)

            if (response.isSuccess()) {
                State.Success(response)
            } else {
                State.Error(
                    exception = Exception(response.error ?: "Unknown error"),
                    message = response.message ?: "Failed to send reset code"
                )
            }
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Failed to request reset code")
        }
    }

    override suspend fun submitResetCode(
        code: String,
        token: String
    ): State<PasswordResetCodeVerifyResponse> {
        return try {
            val response = passwordResetApiService.submitResetCode(code, token)

            if (response.isSuccess()) {
                State.Success(response)
            } else {
                State.Error(
                    exception = Exception(response.error ?: "Invalid code"),
                    message = response.message ?: "Code verification failed"
                )
            }
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Failed to verify code")
        }
    }

    override suspend fun changePassword(
        newPassword: String,
        token: String
    ): State<PasswordChangeResponse> {
        return try {
            val response = passwordResetApiService.changePassword(newPassword, token)

            if (response.isSuccess()) {
                State.Success(response)
            } else {
                State.Error(
                    exception = Exception(response.error ?: "Failed to change password"),
                    message = "Password change failed"
                )
            }
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Failed to change password")
        }
    }
}