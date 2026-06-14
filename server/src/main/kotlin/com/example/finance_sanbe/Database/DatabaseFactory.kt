package com.example.finance_sanbe.Database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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

            if (!TeamItems.selectAll().empty()) {
                TeamItems.deleteAll()
                println("Inserimento manuale")
                //val initTeamItems = listOf(
                //    TeamItem(1, 1, 1, 0),
                //    TeamItem(1, 2, 1, 0),
                //    TeamItem(1, 1, 2, 0),
                //    TeamItem(1, 2, 2, 0),
                //)
//
                //TeamItems.batchInsert(initTeamItems) { teamItem ->
                //    this[TeamItems.id] = teamItem.id
                //    this[TeamItems.itemId] = teamItem.itemId
                //    this[TeamItems.teamId] = teamItem.teamId
                //    this[TeamItems.quantity] = teamItem.quantity
                //}
            }

            if (!Items.selectAll().empty()) {
                Items.deleteAll()
                //println("Inserimento manuale")
                //val initItems = listOf(
                //    ItemStats(1, "cartone", 10, 10, 3, 3),
                //    ItemStats(2, "scotch", 3, 3, 200, 200),
                //)
//
                //Items.batchInsert(initItems) { item ->
                //    this[Items.id] = item.id
                //    this[Items.name] = item.name
                //    this[Items.initialPrice] = item.initialPrice
                //    this[Items.actualPrice] = item.actualPrice
                //    this[Items.initialQuantity] = item.initialQuantity
                //    this[Items.actualQuantity] = item.actualQuantity
                //}
            }
            if (!Team.selectAll().empty()) {
                Team.deleteAll()
                println("Inserimento Teams")
                Team.insert {
                    it[id] = 1
                    it[name] = "Rossi"
                    it[credits] = 0
                }
                Team.insert {
                    it[id] = 2
                    it[name] = "Gialli"
                    it[credits] = 0
                }
                Team.insert {
                    it[id] = 3
                    it[name] = "Blu"
                    it[credits] = 0
                }
                Team.insert {
                    it[id] = 4
                    it[name] = "Fucsia"
                    it[credits] = 0
                }
                Team.insert {
                    it[id] = 5
                    it[name] = "Verdi"
                    it[credits] = 0
                }
                Team.insert {
                    it[id] = 6
                    it[name] = "Azzurri"
                    it[credits] = 0
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
