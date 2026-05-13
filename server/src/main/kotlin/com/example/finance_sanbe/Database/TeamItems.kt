package com.example.finance_sanbe.Database
import org.jetbrains.exposed.sql.Table

object TeamItems: Table("TeamItems") {
    val id = integer("id").autoIncrement()
    val teamId = integer("teamId") references Team.id
    val itemId = integer("itemId") references Items.id
    val quantity = integer("quantity")
}