package me.onvo.onvo.data.network

import io.ktor.client.HttpClient

import io.ktor.client.*
import io.ktor.client.engine.cio.CIO

actual class HttpClientFactory {
    actual fun createClient(): HttpClient = HttpClient(CIO) {
        // same plugins
    }
}