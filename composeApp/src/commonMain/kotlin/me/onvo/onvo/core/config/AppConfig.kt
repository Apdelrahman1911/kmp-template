package me.onvo.onvo.core.config


object AppConfig {
    const val   BASE_URL = "https://yamimanga.me/"
    const val TIMEOUT_MILLIS = 15000L
    const val ENABLE_LOGGING = true

    object Database {
        const val NAME = "app_database.db"
        const val VERSION = 1
    }
}