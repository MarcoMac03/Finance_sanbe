package com.example.finance_sanbe

import com.example.finance_sanbe.Database.DatabaseFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
//import org.example.project.Database.DatabaseFactory
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.example.finance_sanbe.Database.TeamItems
import com.example.finance_sanbe.Database.Items
import org.jetbrains.exposed.sql.lowerCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import com.example.finance_sanbe.Database.Team
import io.ktor.serialization.kotlinx.json.json
import org.jetbrains.exposed.sql.and
import com.example.finance_sanbe.NewItem
import com.example.finance_sanbe.MarketAction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.div
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.times
import org.jetbrains.exposed.sql.update

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    routing {
        get("/itemsAll") {
            val items = transaction {
                Items.selectAll()
                    .map { row ->
                        ItemStats(
                            id = row[Items.id],
                            name = row[Items.name],
                            initialPrice = row[Items.initialPrice],
                            actualPrice = row[Items.actualPrice],
                            initialQuantity = row[Items.initialQuantity],
                            actualQuantity = row[Items.actualQuantity]
                        )
                    }
            }
            call.respond(items)
        }

        post("/item") {
            val item = call.receive<NewItem>()
            if(item.name.isBlank() || item.price == 0 || item.quantity == 0) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            transaction {
                Items.insert {
                    it[name] = item.name
                    it[initialPrice] = item.price
                    it[actualPrice] = item.price
                    it[initialQuantity] = item.quantity
                    it[actualQuantity] = item.quantity
                }
            }
            call.respond(HttpStatusCode.Created)
        }

        post("/market") {
            val action = call.receive<MarketAction>()
            if(action.type == ActionType.BUY) {
                val actualQuantity = transaction {
                    val q = Items.select(Items.actualQuantity).where{Items.id eq action.itemId}
                    q.first()[Items.actualQuantity]
                }
                if(actualQuantity < action.quantity) {
                    call.respond(HttpStatusCode.BadRequest, "Quantità richiesta eccessiva")
                    return@post
                }

                val teamCredits = transaction {
                    val q = Team.select(Team.credits).where{Team.id eq action.teamId}
                    q.first()[Team.credits]
                }
                if(teamCredits < (action.quantity * action.price)) {
                    call.respond(HttpStatusCode.BadRequest, "Non abbastanza crediti")
                    return@post
                }

                val update = transaction {
                    Items.update({Items.id eq action.itemId}) { it[Items.actualQuantity] = actualQuantity - action.quantity }
                    Team.update({Team.id eq action.teamId}) { it[Team.credits] = Team.credits - (action.price * action.quantity)}
                    Items.update({Items.id eq action.itemId}) { it[Items.actualPrice] = Items.actualPrice + (Items.actualPrice * 5 / 100) }
                    TeamItems.update({(TeamItems.itemId eq action.itemId) and (TeamItems.teamId eq action.teamId)}) { it[TeamItems.quantity] = TeamItems.quantity + action.quantity }
                }
                if(update == 0) {
                    call.respond(HttpStatusCode.InternalServerError)
                    return@post
                }
                call.respond(HttpStatusCode.OK)
            } else {
                val teamQuantity = transaction {
                    (Items innerJoin TeamItems innerJoin Team).select(TeamItems.quantity).where{ (Items.id eq action.teamId) and (Team.id eq action.teamId)}.first()[TeamItems.quantity]
                }
                if(teamQuantity < action.quantity) {
                    call.respond(HttpStatusCode.Conflict, "Mismatch tra quantità richiesta e disponibile nella squadra")
                    return@post
                }
                val priceToRefund = action.price * action.quantity
                val update = transaction {
                    Items.update({Items.id eq action.itemId}) { it[Items.actualQuantity] = Items.actualQuantity + action.quantity }
                    Team.update({Team.id eq action.teamId}) { it[Team.credits] = Team.credits + priceToRefund }
                    TeamItems.update({(TeamItems.itemId eq action.itemId) and (TeamItems.teamId eq action.teamId)}) { it[TeamItems.quantity] = TeamItems.quantity - action.quantity }
                }
                if(update == 0) {
                    call.respond(HttpStatusCode.InternalServerError)
                    return@post
                }
                call.respond(HttpStatusCode.OK)
            }

        }
    }
}