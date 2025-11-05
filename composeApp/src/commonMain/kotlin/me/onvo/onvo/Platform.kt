package me.onvo.onvo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform