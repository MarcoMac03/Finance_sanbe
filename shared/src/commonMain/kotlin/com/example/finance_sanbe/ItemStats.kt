package com.example.finance_sanbe

import kotlinx.serialization.Serializable

@Serializable
data class ItemStats (
    val id: Int,
    val name: String,
    val initialPrice: Int,
    val actualPrice: Int,
    val initialQuantity: Int,
    val actualQuantity: Int
)