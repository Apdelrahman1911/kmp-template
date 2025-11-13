// File: commonMain/kotlin/me/onvo/onvo/domain/model/Profile.kt
package me.onvo.onvo.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ========== Auth Status Response (Simple Profile) ==========
@Serializable
data class AuthStatusResponse(
    val status: String,
    val isLoged: Boolean,
    val plus: PlusInfo,
    val data: AuthUserData
)

@Serializable
data class AuthUserData(
    val id: Int,
    @SerialName("session_id") val sessionId: String,
    val username: String,
    val image: String,
    val fullname: String,
    val firstname: String,
    val bio: String,
    val counts: UserCounts,
    val settings: UserSettings
)

// ========== Full User Profile Response ==========
@Serializable
data class UserProfileResponse(
    val user: UserProfile,
    val links: List<UserLink> = emptyList()
)

@Serializable
data class UserProfile(
    val statue: String? = null,
    val id: Int,
    val usnm: String,
    val username: String,
    val fnme: String,
    val fullname: String,
    val firstname: String,
    val img: String,
    val image: String,
    val bio: String,
    val lnks: List<UserLink> = emptyList(),
    val cnt: UserProfileCounts,
    val vrfy: String,
    @SerialName("is_verified") val isVerified: Boolean,
    val charms: String,
    val flw: String,
    val followed: Boolean,
    @SerialName("followed_back") val followedBack: Boolean,
    val mute: Int,
    val muted: Int,
    val tmp: String,
    val holder: String,
    val story: String? = null
)

@Serializable
data class UserProfileCounts(
    val colls: String,
    val msg: String,
    val likes: String,
    val views: String,
    val followers: String,
    val following: String
)

@Serializable
data class UserLink(
    val t: String,
    val type: String,
    val d: String,  // display text
    val url: String
) {
    // Helper to get icon based on type
    val linkType: LinkType
        get() = when (type.lowercase()) {
            "ln" -> LinkType.LINKEDIN
            "wa" -> LinkType.WHATSAPP
            "sp" -> LinkType.SPOTIFY
            "sc" -> LinkType.SNAPCHAT
            "ig" -> LinkType.INSTAGRAM
            "rd" -> LinkType.REDDIT
            "sn" -> LinkType.SOUND_CLOUD
            "pt" -> LinkType.PINTEREST
            "gh" -> LinkType.GITHUB
            "tw" -> LinkType.TWITTER
            "gm" -> LinkType.GMAIL
            "bh" -> LinkType.BEHANCE
            "yt" -> LinkType.YOUTUBE
            "tk" -> LinkType.TIKTOK
            "an" -> LinkType.ANIME
            "fb" -> LinkType.FACEBOOK
            else -> LinkType.OTHER
        }
}

enum class LinkType(val displayName: String) {
    LINKEDIN("LinkedIn"),
    WHATSAPP("WhatsApp"),
    SPOTIFY("Spotify"),
    SNAPCHAT("Snapchat"),
    INSTAGRAM("Instagram"),
    REDDIT("Reddit"),
    SOUND_CLOUD("SoundCloud"),
    PINTEREST("Pinterest"),
    GITHUB("GitHub"),
    TWITTER("Twitter"),
    GMAIL("Gmail"),
    BEHANCE("Behance"),
    YOUTUBE("YouTube"),
    TIKTOK("TikTok"),
    ANIME("Anime"),
    FACEBOOK("Facebook"),
    OTHER("Link")
}

// ========== UI State Models ==========
sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class AuthStatusUiState {
    data object Loading : AuthStatusUiState()
    data class Success(val user: AuthUserData) : AuthStatusUiState()
    data class Error(val message: String) : AuthStatusUiState()
}