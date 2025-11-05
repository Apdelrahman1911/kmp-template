package me.onvo.onvo.domain.model

import kotlinx.serialization.Serializable


@Serializable
enum class SourceState {
    WORKING,
    UNDER_MAINTENANCE,
    STOPPED
}