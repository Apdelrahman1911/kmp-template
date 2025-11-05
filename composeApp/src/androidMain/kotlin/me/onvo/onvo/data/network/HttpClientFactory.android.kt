package me.onvo.onvo.data.network

// androidMain

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.*

actual class HttpClientFactory {
    actual fun createClient(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) { logger = Logger.SIMPLE; level = LogLevel.INFO }
        install(HttpTimeout) { requestTimeoutMillis = 15000 }
        engine {
            // optional: configure OkHttpClient via preconfigured if you want cert pinning, interceptors, etc.
        }
    }
}
