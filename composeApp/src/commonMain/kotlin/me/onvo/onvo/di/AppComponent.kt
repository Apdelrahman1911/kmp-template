//package me.onvo.onvo.di
//
//
//
//import dev.zacsweers.metro.AppScope
//import dev.zacsweers.metro.DependencyGraph
//import dev.zacsweers.metro.Provides
//import dev.zacsweers.metro.SingleIn
//import dev.zacsweers.metro.createGraph
//import io.ktor.client.HttpClient
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.serialization.json.Json
//import me.onvo.onvo.data.network.ApiService
//import me.onvo.onvo.data.network.ApiServiceImpl
//import me.onvo.onvo.data.network.HttpClientFactory
//import me.onvo.onvo.presentation.viewmodel.SourcesViewModel
//
//@DependencyGraph(AppScope::class)
//@SingleIn(AppScope::class)
//abstract class AppGraph {
//
//    // Expose ViewModel to UI
//    abstract val sourcesViewModel: SourcesViewModel
//
//    // Network providers
//    @Provides
//    @SingleIn(AppScope::class)
//    fun provideJson(): Json = Json {
//        ignoreUnknownKeys = true
//        isLenient = true
//        coerceInputValues = true
//        prettyPrint = false
//    }
//
//    @Provides
//    @SingleIn(AppScope::class)
//    fun provideHttpClient(json: Json): HttpClient {
//        return HttpClientFactory().createClient().config {
//            install(ContentNegotiation) {
//                json(json)
//            }
//        }
//    }
//
//    @Provides
//    @SingleIn(AppScope::class)
//    fun provideBaseUrl(): String = "https://yamimanga.me/"
//
//    @Provides
//    @SingleIn(AppScope::class)
//    fun provideApiService(
//        client: HttpClient,
//        baseUrl: String,
//        json: Json
//    ): ApiService = ApiServiceImpl(client, baseUrl, json)
//
//    companion object {
//        fun create(): AppGraph = createGraph<AppGraph>()
//    }
//}