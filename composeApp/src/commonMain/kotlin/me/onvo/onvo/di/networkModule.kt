package me.onvo.onvo.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.onvo.onvo.data.network.ApiService
import me.onvo.onvo.data.network.ApiServiceImpl
import me.onvo.onvo.data.network.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule: Module = module {

    // 1️⃣ Provide a shared Json instance
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            prettyPrint = false
        }
    }

    // 2️⃣ Provide HttpClient — use injected Json for content negotiation
    single<HttpClient> {
        val json = get<Json>()
        HttpClientFactory().createClient().config {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    // 3️⃣ Provide baseUrl
    single(named("baseUrl")) { "https://yamimanga.me/" }

    // 4️⃣ ApiServiceImpl uses injected HttpClient and baseUrl
    single<ApiService> { ApiServiceImpl(
        get(), get(named("baseUrl")),
        json = get()                  // Json instance

        ) }

    // 5️⃣ (Optional) If you have repositories depending on ApiService
    // single<ItemsRepository> { ItemsRepositoryImpl(get()) }
}
