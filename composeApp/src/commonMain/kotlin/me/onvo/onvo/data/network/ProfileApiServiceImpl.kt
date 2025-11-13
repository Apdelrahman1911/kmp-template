package me.onvo.onvo.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import me.onvo.onvo.domain.model.AuthStatusResponse
import me.onvo.onvo.domain.model.UserProfileResponse

class ProfileApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : ProfileApiService {

    override suspend fun getAuthStatus(token: String): AuthStatusResponse {
        val response: HttpResponse = client.get("${baseUrl}v2/auth/status") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        // Check if unauthorized (401)
        if (response.status == HttpStatusCode.Unauthorized) {
            throw UnauthorizedException("Session expired or invalid token")
        }

        return response.body()
    }

    override suspend fun getUserProfile(userId: Int, token: String?): UserProfileResponse {
        return client.get("${baseUrl}v2/users") {
            contentType(ContentType.Application.Json)
            parameter("id", userId)
            token?.let { bearerAuth(it) }
        }.body()
    }
}

// Custom exception for unauthorized access
class UnauthorizedException(message: String) : Exception(message)