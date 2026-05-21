package com.example.finance_sanbe
import kotlinx.serialization.Serializable

enum class ActionType { COMPRA, VENDI }

@Serializable
data class MarketAction(
    val itemId: Int = -1,
    val teamId: Int = -1,
    val type: ActionType = ActionType.COMPRA,
    val quantity: Int = -1,
    val price: Int = -1
)