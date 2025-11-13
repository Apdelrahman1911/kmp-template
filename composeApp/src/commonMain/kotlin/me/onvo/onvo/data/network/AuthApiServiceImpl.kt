
// File: commonMain/kotlin/me/onvo/onvo/data/network/AuthApiService.kt
package me.onvo.onvo.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.onvo.onvo.domain.model.CheckInputRequest
import me.onvo.onvo.domain.model.CheckInputResponse
import me.onvo.onvo.domain.model.LoginRequest
import me.onvo.onvo.domain.model.LoginResponse
import me.onvo.onvo.domain.model.TokenRequest
import me.onvo.onvo.domain.model.TokenResponse

class AuthApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : AuthApiService {

    override suspend fun getToken(request: TokenRequest): TokenResponse {
        return client.post("${baseUrl}token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun checkInput(input: String, token: String): CheckInputResponse {
        return client.post("${baseUrl}v2/auth/check") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(CheckInputRequest(input))
        }.body()
    }

    override suspend fun login(request: LoginRequest, token: String): LoginResponse {
        return client.post("${baseUrl}v2/auth/login") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(request)
        }.body()
    }

    override suspend fun logout(token: String) {
        client.post("${baseUrl}v2/auth/logout") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }
    }
}