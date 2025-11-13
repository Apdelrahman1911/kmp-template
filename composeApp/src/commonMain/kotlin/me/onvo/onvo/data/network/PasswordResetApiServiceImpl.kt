package me.onvo.onvo.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.onvo.onvo.domain.model.PasswordChangeRequest
import me.onvo.onvo.domain.model.PasswordChangeResponse
import me.onvo.onvo.domain.model.PasswordResetCodeResponse
import me.onvo.onvo.domain.model.PasswordResetCodeSubmit
import me.onvo.onvo.domain.model.PasswordResetCodeVerifyResponse
import me.onvo.onvo.domain.model.PasswordResetRequest


class PasswordResetApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : PasswordResetApiService {

    override suspend fun requestResetCode(
        userId: String,
        token: String
    ): PasswordResetCodeResponse {
        return client.post("${baseUrl}v2/auth/reset/request") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(PasswordResetRequest(userId))
        }.body()
    }

    override suspend fun submitResetCode(
        code: String,
        token: String
    ): PasswordResetCodeVerifyResponse {
        return client.post("${baseUrl}v2/auth/reset/submit") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(PasswordResetCodeSubmit(code))
        }.body()
    }

    override suspend fun changePassword(
        newPassword: String,
        token: String
    ): PasswordChangeResponse {
        return client.post("${baseUrl}v2/auth/reset/change") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(PasswordChangeRequest(newPassword))
        }.body()
    }
}