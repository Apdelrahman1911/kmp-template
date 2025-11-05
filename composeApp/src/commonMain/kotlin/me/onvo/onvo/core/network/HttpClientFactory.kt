package me.onvo.onvo.core.network

import io.ktor.client.HttpClient

expect class HttpClientFactory() {
    fun create(): HttpClient
}

// Extension for easy access
fun HttpClientFactory.createConfigured(): HttpClient = create()