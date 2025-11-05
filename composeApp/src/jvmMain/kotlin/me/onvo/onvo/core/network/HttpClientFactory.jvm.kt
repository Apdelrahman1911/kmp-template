package me.onvo.onvo.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

actual class HttpClientFactory {
    actual fun create(): HttpClient = HttpClient(CIO) {
        // same plugins
    }
}