package org.lineageos.twelve.utils.ktorclient

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

object KtorClient {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
    }
}