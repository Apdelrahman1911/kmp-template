package me.onvo.onvo.domain.usecase.profile

import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.AuthUserData
import me.onvo.onvo.domain.repository.ProfileRepository

class GetCurrentUserStatusUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): State<AuthUserData> {
        return profileRepository.getCurrentUserStatus()
    }
}