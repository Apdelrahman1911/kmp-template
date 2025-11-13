package me.onvo.onvo.domain.usecase.profile

import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.UserProfile
import me.onvo.onvo.domain.repository.ProfileRepository

class GetUserProfileUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: Int): State<UserProfile> {
        if (userId <= 0) {
            return State.Error(
                IllegalArgumentException("Invalid user ID"),
                "Please provide a valid user ID"
            )
        }
        return profileRepository.getUserProfile(userId)
    }
}