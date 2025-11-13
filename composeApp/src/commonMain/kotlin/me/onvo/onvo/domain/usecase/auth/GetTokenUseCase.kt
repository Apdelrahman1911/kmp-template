package me.onvo.onvo.domain.usecase.auth

import me.onvo.onvo.core.device.DeviceInfoProvider
import me.onvo.onvo.core.util.State
import me.onvo.onvo.domain.model.TokenResponse
import me.onvo.onvo.domain.repository.AuthRepository

class GetTokenUseCase(
    private val authRepository: AuthRepository,
    private val deviceInfoProvider: DeviceInfoProvider
) {
    suspend operator fun invoke(): State<TokenResponse> {
        val deviceInfo = deviceInfoProvider.getDeviceInfo()
        return when (val result = authRepository.getToken(deviceInfo)) {
            is State.Success -> {
                // Save token for future use
                authRepository.saveAuthToken(result.data.token)
                result
            }
            is State.Error -> result
            is State.Loading -> result
        }
    }
}