package me.onvo.onvo.core.network



import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.*


actual class HttpClientFactory {
    actual fun create(): HttpClient = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) { logger = Logger.SIMPLE; level = LogLevel.INFO }
        install(HttpTimeout) { requestTimeoutMillis = 15000 }
        engine {
            configureRequest { // optional: configure darwin-specific things
            }
        }
    }


}
