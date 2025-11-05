package me.onvo.onvo.data.network

// commonMain

import io.ktor.client.*

expect class HttpClientFactory() {
    fun createClient(): HttpClient
}
