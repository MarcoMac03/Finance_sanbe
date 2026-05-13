package com.example.finance_sanbe.Database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.example.finance_sanbe.ItemStats
import com.example.finance_sanbe.TeamItem
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*

// Initialize the database when the server starts
object DatabaseFactory {
    fun init() {
        val rawUrl = System.getenv("DATABASE_URL")

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"

            if (rawUrl != null) {
                try {
                    val cleanUrl = rawUrl.trim()

                    val protocolRemoved = cleanUrl.replace("postgresql://", "")

                    val parts = protocolRemoved.split("@")

                    val credentials = parts[0].split(":")
                    val username = credentials[0]
                    val password = credentials.drop(1).joinToString(":")

                    val hostAndRest = parts[1].split("/")
                    val hostAndPort = hostAndRest[0]
                    val dbName = hostAndRest[1]

                    val hostParts = hostAndPort.split(":")
                    val host = hostParts[0]
                    val port = if (hostParts.size > 1) ":${hostParts[1]}" else ""

                    jdbcUrl = "jdbc:postgresql://$host$port/$dbName"
                    this.username = username
                    this.password = password

                } catch (e: Exception) {
                    println("Errore durante il parsing dell'URL, uso i parametri di fallback: ${e.message}")
                    setupLocalFallback()
                }
            } else {
                setupLocalFallback()
            }

            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val ds = HikariDataSource(config)
        Database.connect(ds)

        transaction {
            SchemaUtils.create(Items, Team, TeamItems)

            if (Items.selectAll().empty()) {
                println("Inserimento manuale")
                val initItems = listOf(
                    ItemStats(1, "cartone", 10, 10, 3, 3),
                    ItemStats(2, "scotch", 3, 3, 200, 200),
                )

                Items.batchInsert(initItems) { item ->
                    this[Items.id] = item.id
                    this[Items.name] = item.name
                    this[Items.initialPrice] = item.initialPrice
                    this[Items.actualPrice] = item.actualPrice
                    this[Items.initialQuantity] = item.initialQuantity
                    this[Items.actualQuantity] = item.actualQuantity
                }
            }
            if (Team.selectAll().empty()) {
                println("Inserimento User")
                Team.insert {
                    it[id] = 1
                    it[name] = "Rossi"
                    it[credits] = 100
                }
                Team.insert {
                    it[id] = 2
                    it[name] = "Gialli"
                    it[credits] = 20
                }
            }
            if (TeamItems.selectAll().empty()) {
                println("Inserimento manuale")
                val initTeamItems = listOf(
                    TeamItem(1, 1, 1, 2),
                    TeamItem(1, 2, 1, 0),
                    TeamItem(1, 1, 2, 0),
                    TeamItem(1, 2, 2, 20),
                )

                TeamItems.batchInsert(initTeamItems) { teamItem ->
                    this[TeamItems.id] = teamItem.id
                    this[TeamItems.itemId] = teamItem.itemId
                    this[TeamItems.teamId] = teamItem.teamId
                    this[TeamItems.quantity] = teamItem.quantity
                }
            }
        }
    }

    private fun HikariConfig.setupLocalFallback() {
        jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
        username = "postgres"
        password = "admin"
    }
}
