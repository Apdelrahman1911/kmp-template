package me.onvo.onvo.domain.repository

import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.AuthUserData
import me.onvo.onvo.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getCurrentUserStatus(): State<AuthUserData>
    suspend fun getUserProfile(userId: Int): State<UserProfile>
    suspend fun clearSession()
}