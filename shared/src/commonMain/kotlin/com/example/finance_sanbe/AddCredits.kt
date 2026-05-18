package com.example.finance_sanbe

import kotlinx.serialization.Serializable

@Serializable
data class AddCredits (
    val teamId: Int = -1,
    val credits: Int = 0
)