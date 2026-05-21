package com.example.finance_sanbe
import kotlinx.serialization.Serializable

@Serializable
data class MarketStats(
    val price: Int = -1,
    val quantity: Int = -1,
)