package com.example.finance_sanbe
import kotlinx.serialization.Serializable

@Serializable
data class TeamItem(
    val id: Int,
    val itemId: Int,
    val teamId: Int,
    val quantity: Int
)