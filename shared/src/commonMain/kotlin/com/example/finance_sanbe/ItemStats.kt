package com.example.finance_sanbe

import kotlinx.serialization.Serializable

@Serializable
data class ItemStats (
    val id: Int = -1,
    val name: String = "",
    val initialPrice: Int? = null,
    val actualPrice: Int = -1,
    val initialQuantity: Int? = null,
    val actualQuantity: Int = -1,
    val maxPrice: Int? = null
)