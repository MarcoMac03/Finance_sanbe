package com.example.finance_sanbe
import kotlinx.serialization.Serializable

enum class ActionType { BUY, SELL }

@Serializable
data class MarketAction(
    val itemId: Int,
    val teamId: Int,
    val type: ActionType,
    val quantity: Int,
    val price: Int
)