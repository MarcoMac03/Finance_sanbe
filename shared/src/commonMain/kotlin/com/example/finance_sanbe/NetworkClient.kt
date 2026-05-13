package com.example.finance_sanbe

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json

object NetworkClient {

    const val BASE_URL = "https://finance-sanbe.onrender.com"
    val client = HttpClient {
        install(ContentNegotiation) { json() }
    }
}