// File: commonMain/kotlin/me/onvo/onvo/domain/model/auth.kt
package me.onvo.onvo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val deviceId: String,
    val brand: String,
    val model: String,
    val systemName: String,
    val systemVersion: String,
    val appVersion: String,
    val buildNumber: String,
    val uniqueId: String,
    val deviceName: String,
    val isTablet: Boolean,
    val carrier: String?,
    val timezone: String
)

@Serializable
data class TokenRequest(
    val info: DeviceInfo
)

@Serializable
data class TokenResponse(
    val token: String,
    val vuse: String? = null
)

@Serializable
data class CheckInputRequest(
    val input: String
)

// Updated response model to handle both success and error
@Serializable
data class CheckInputResponse(
    val id: Int? = null,
    val fullname: String? = null,
    val image: String? = null,
    val username: String? = null,
    // Error fields
    val error: String? = null,
    val type: String? = null,
    val message: String? = null
) {
    fun isSuccess(): Boolean = error == null && id != null
    fun isError(): Boolean = error != null
}

@Serializable
data class LoginRequest(
    val id: String,
    val password: String // MD5 hashed
)

@Serializable
data class PlusInfo(
    val status: String? = null,
    val billing_period: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val external_id: String? = null,
    val payment_method: String? = null
)

@Serializable
data class UserCounts(
    val messages: Int = 0,
    val archives: Int = 0,
    val sent: Int = 0,
    val notifications: Int = 0
)

@Serializable
data class UserSettings(
    val theme: String? = null,
    val token: String? = null,
    val them: String? = null,
    val lang: String? = null,
    val ctm: String? = null,
    val aoe: String? = null,
    val reminder: Int? = null
)

@Serializable
data class SessionUser(
    val id: Int,
    val session_id: String,
    val username: String,
    val image: String,
    val fullname: String,
    val firstname: String? = null,
    val bio: String? = null,
    val counts: UserCounts? = null,
    val settings: UserSettings? = null
)

// Updated response model to handle both success and error
@Serializable
data class LoginResponse(
    val status: String? = null,
    val isLoged: Boolean = false,
    val plus: PlusInfo? = null,
    val data: SessionUser? = null,
    // Error fields
    val error: String? = null,
    val type: String? = null,
    val message: String? = null
) {
    fun isSuccess(): Boolean = isLoged && error == null
    fun isError(): Boolean = error != null
}

data class AuthState(
    val isAuthenticated: Boolean = false,
    val userId: String? = null,
    val userName: String? = null,
    val authToken: String? = null
)