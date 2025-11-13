package me.onvo.onvo.data.network

import me.onvo.onvo.domain.model.AuthStatusResponse
import me.onvo.onvo.domain.model.UserProfileResponse

interface ProfileApiService {
    suspend fun getAuthStatus(token: String): AuthStatusResponse
    suspend fun getUserProfile(userId: Int, token: String? = null): UserProfileResponse
}