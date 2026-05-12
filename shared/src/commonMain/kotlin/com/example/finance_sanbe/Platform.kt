package com.example.finance_sanbe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform