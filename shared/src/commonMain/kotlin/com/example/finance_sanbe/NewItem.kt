package com.example.finance_sanbe

import kotlinx.serialization.Serializable

@Serializable
data class NewItem (
    val name: String = "",
    val quantity: Int = 0,
    val price: Int = 0
)