package com.example.finance_sanbe.Database
import org.jetbrains.exposed.sql.Table

object Team: Table("Team") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val credits = integer("credits")

    override val primaryKey = PrimaryKey(id)
}