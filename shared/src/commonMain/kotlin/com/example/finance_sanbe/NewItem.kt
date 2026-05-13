package com.example.finance_sanbe

import kotlinx.serialization.Serializable

@Serializable
data class NewItem (
    val name: String,
    val quantity: Int,
    val price: Int
)