package me.onvo.onvo.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.onvo.onvo.core.config.AppConfig
import me.onvo.onvo.core.network.HttpClientFactory
import me.onvo.onvo.data.network.ApiService
import me.onvo.onvo.data.network.ApiServiceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*

val networkModule = module {

    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            prettyPrint = false
        }
    }

    // register baseUrl as a named String
    single(named("baseUrl")) { AppConfig.BASE_URL }

    single<HttpClient> {
        HttpClientFactory().create().config {
            install(ContentNegotiation) { json(get()) }
            if (AppConfig.ENABLE_LOGGING) {
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.INFO
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = AppConfig.TIMEOUT_MILLIS
            }
            defaultRequest {
                url(AppConfig.BASE_URL)
            }
        }
    }

    single<ApiService> {
        // get() -> HttpClient, get(named("baseUrl")) -> String, get() -> Json
        ApiServiceImpl(get(), get(named("baseUrl")), json = get())
    }
}

