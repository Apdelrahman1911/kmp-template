package me.onvo.onvo.data.repository

import me.onvo.onvo.core.datastore.PreferencesManager
import me.onvo.onvo.core.util.State
import me.onvo.onvo.data.network.ProfileApiService
import me.onvo.onvo.data.network.UnauthorizedException
import me.onvo.onvo.domain.model.AuthUserData
import me.onvo.onvo.domain.model.UserProfile
import me.onvo.onvo.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileApiService: ProfileApiService,
    private val preferencesManager: PreferencesManager
) : ProfileRepository {

    override suspend fun getCurrentUserStatus(): State<AuthUserData> {
        return try {
            val token = preferencesManager.getAuthToken()
                ?: return State.Error(
                    IllegalStateException("No auth token"),
                    "Please login first"
                )

            val response = profileApiService.getAuthStatus(token)

            if (response.isLoged) {
                State.Success(response.data)
            } else {
                State.Error(
                    IllegalStateException("Not logged in"),
                    "Session expired. Please login again"
                )
            }
        } catch (e: UnauthorizedException) {
            // Clear token on unauthorized
            preferencesManager.clearAuthToken()
            preferencesManager.clearUserSession()
            State.Error(e, "Session expired. Please login again")
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Failed to get user status")
        }
    }

    override suspend fun getUserProfile(userId: Int): State<UserProfile> {
        return try {
            val token = preferencesManager.getAuthToken()
            val response = profileApiService.getUserProfile(userId, token)
            State.Success(response.user)
        } catch (e: Exception) {
            State.Error(e, e.message ?: "Failed to load profile")
        }
    }

    override suspend fun clearSession() {
        preferencesManager.clearAuthToken()
        preferencesManager.clearUserSession()
    }
}