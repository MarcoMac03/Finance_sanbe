package com.example.finance_sanbe.Database
import org.jetbrains.exposed.sql.Table

object Items:Table("Items") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30)
    val initialPrice = integer("initialPrice")
    val actualPrice = integer("actualPrice")
    val initialQuantity = integer("initialQuantity")
    val actualQuantity = integer("actualQuantity")

    override val primaryKey = PrimaryKey(id)
}