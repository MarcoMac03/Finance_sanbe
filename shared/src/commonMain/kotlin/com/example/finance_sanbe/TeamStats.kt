package com.example.finance_sanbe
import kotlinx.serialization.Serializable

@Serializable
data class TeamStats(
    val teamId: Int,
    val name: String,
    val credits: Int
)