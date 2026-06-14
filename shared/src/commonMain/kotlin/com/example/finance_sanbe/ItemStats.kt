package com.example.finance_sanbe

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class ItemStats @OptIn(ExperimentalSerializationApi::class) constructor(
    val id: Int = -1,
    val name: String = "",
    val initialPrice: Int? = null,
    val actualPrice: Int = -1,
    val initialQuantity: Int? = null,
    val actualQuantity: Int = -1,

    @EncodeDefault
    val maxPrice: Int? = null
)