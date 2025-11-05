package me.onvo.onvo.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import me.onvo.onvo.domain.model.Source

class ApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String,
    private val json: Json
) : ApiService {
    override suspend fun fetchItems() : List<Source> {
        val raw = client.get("${baseUrl}source").bodyAsText()
        return try {
            val items = json.decodeFromString(ListSerializer(Source.Serializer), raw)
            items
        } catch (e: Exception) {
            println("❌ Error decoding response: ${e.message}\nRaw data: $raw")

            emptyList()
        }
    }
}