package com.example.finance_sanbe.sections

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finance_sanbe.ActionType
import com.example.finance_sanbe.BackgroundColor
import com.example.finance_sanbe.ItemStats
import com.example.finance_sanbe.MarketAction
import com.example.finance_sanbe.NetworkClient
import com.example.finance_sanbe.PrimaryColor
import com.example.finance_sanbe.SecondaryColor
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsMarket() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var expandedTeam by remember { mutableStateOf(false) }
    var expandedItems by remember { mutableStateOf(false) }
    var expandedActions by remember { mutableStateOf(false) }
    val actionTypes = remember { ActionType.entries }
    var item by remember { mutableStateOf(ItemStats()) }
    var action by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var teamList by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var itemList by remember { mutableStateOf<List<ItemStats>>(emptyList()) }
    var team by remember { mutableStateOf("") }
    var selectedTeam by remember { mutableStateOf<Int?>(null) }
    var market by remember { mutableStateOf(MarketAction()) }
    var quantity by remember { mutableIntStateOf(0) }
    var teamError by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }
    var itemError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(expandedTeam) {
        if(expandedTeam && teamList.isEmpty()) {
            try {
                teamList = NetworkClient.client.get("${NetworkClient.BASE_URL}/teams").body()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            }
        }
    }

    LaunchedEffect(expandedItems, action) {
        if(expandedItems && itemList.isEmpty() && selectedTeam != null && action.isNotEmpty()) {
            try {
                itemList = NetworkClient.client.get("${NetworkClient.BASE_URL}/itemsByAction/${selectedTeam}/${action}").body()
                Log.d("Items list", "Items list: ${itemList.size}")
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            }
        }
    }

    /*LaunchedEffect(item) {
        if(!item.isEmpty() && !action.isEmpty() && selectedTeam != null) {
            try {
                itemPQ = NetworkClient.client.get("${NetworkClient.BASE_URL}/itemStats/${market.itemId}/${market.type}/${selectedTeam}").body()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            }
        }
    }*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(scrollState)
            .padding(top = 20.dp, start = 30.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Borsa di Sanbe",
            color = PrimaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 30.dp)
        )
        Spacer(modifier = Modifier.padding(top = 30.dp))

        Text(text = "Seleziona squadra", color = PrimaryColor, fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(top = 10.dp))

        // selezione team
        ExposedDropdownMenuBox(
            expanded = expandedTeam,
            onExpandedChange = { expandedTeam = it }
        ) {
            OutlinedTextField(
                value = team,
                onValueChange = {},
                readOnly = true,
                label = { Text("Squadra", color = PrimaryColor, fontSize = 14.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeam) },
                modifier = Modifier
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = PrimaryColor,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = PrimaryColor,
                    unfocusedTextColor = PrimaryColor,
                ),
            )

            ExposedDropdownMenu(
                expanded = expandedTeam,
                onDismissRequest = { expandedTeam = false },
                modifier = Modifier.heightIn(max = 200.dp),
            ) {
                teamList.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.second) },
                        onClick = {
                            team = selectionOption.second
                            selectedTeam = selectionOption.first
                            itemList = emptyList()
                            itemName = ""
                            market = market.copy(teamId = selectionOption.first)
                            expandedTeam = false
                        }
                    )
                }
            }
        }
        AnimatedVisibility(visible = teamError) {
            Text(
                text = "Seleziona una squadra",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))

        // selezione tipologia di operazione (buy, sell)
        Text(text = "Seleziona operazione", color = PrimaryColor, fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(top = 10.dp))

        ExposedDropdownMenuBox(
            expanded = expandedActions,
            onExpandedChange = { expandedActions = it }
        ) {
            OutlinedTextField(
                value = action,
                onValueChange = {},
                readOnly = true,
                label = { Text("Compra / Vendi", color = PrimaryColor, fontSize = 14.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActions) },
                modifier = Modifier
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = PrimaryColor,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = PrimaryColor,
                    unfocusedTextColor = PrimaryColor,
                ),
            )

            ExposedDropdownMenu(
                expanded = expandedActions,
                onDismissRequest = { expandedActions = false },
                modifier = Modifier.heightIn(max = 200.dp),
            ) {
                actionTypes.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.name) },
                        onClick = {
                            action = selectionOption.name
                            itemList = emptyList()
                            itemName = ""
                            market = market.copy(type = selectionOption)
                            expandedActions = false
                        }
                    )
                }
            }
        }
        AnimatedVisibility(visible = actionError) {
            Text(
                text = "Seleziona una operazione",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))

        if(selectedTeam != null && action.isNotEmpty()) {
            Text(text = "Seleziona oggetto", color = PrimaryColor, fontSize = 18.sp)
            Spacer(modifier = Modifier.padding(top = 10.dp))

            ExposedDropdownMenuBox(
                expanded = expandedItems,
                onExpandedChange = { expandedItems = it }
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Oggetto", color = PrimaryColor, fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedItems) },
                    modifier = Modifier
                        .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = PrimaryColor,
                        unfocusedTextColor = PrimaryColor,
                    ),
                )

                ExposedDropdownMenu(
                    expanded = expandedItems,
                    onDismissRequest = { expandedItems = false },
                    modifier = Modifier.heightIn(max = 200.dp),
                ) {
                    itemList.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.name) },
                            onClick = {
                                Log.d("Item selected", "Item selected for ${action}: ${selectionOption}")
                                itemName = selectionOption.name
                                market = market.copy(itemId = selectionOption.id)
                                item = selectionOption
                                market = market.copy(price = quantity * market.price)
                                expandedItems = false
                            }
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = itemError) {
            Text(
                text = "Seleziona un oggetto",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))

        if(itemName.isNotBlank() && action.isNotEmpty() && selectedTeam != null) {
            Box(Modifier
                .padding(top = 10.dp, bottom = 20.dp, start = 30.dp, end = 30.dp)
                .background(BackgroundColor)
                .fillMaxWidth(),
                contentAlignment = Alignment.Center,

                ) {
                Text(text = "Prezzo: ${market.price}", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart))

                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Quantità", color = PrimaryColor, fontSize = 14.sp) },
                    trailingIcon = {
                        Row(Modifier.padding(end = 2.dp)) {
                            IconButton(
                                onClick = {
                                    if(quantity > 0) {
                                        quantity--
                                        market = market.copy(quantity = quantity)
                                        market = market.copy(price = quantity * item.actualPrice)
                                    }
                                }
                            ) { Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Diminuisci", tint = if(quantity > 1) PrimaryColor else Color.Gray) }
                            IconButton(
                                onClick = {
                                    if(quantity < item.actualQuantity && (action == "VENDI" || (item.maxPrice != null && market.price < item.maxPrice!!))) {
                                        quantity++
                                        market = market.copy(quantity = quantity)
                                        market = market.copy(price = quantity * item.actualPrice)
                                    }
                                }
                            ) { Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Aumenta", tint = PrimaryColor) }
                        }
                    },
                    modifier = Modifier
                        .width(140.dp)
                        .align(Alignment.CenterEnd),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = PrimaryColor,
                        unfocusedTextColor = PrimaryColor,
                    )
                )
            }
            AnimatedVisibility(visible = quantityError) {
                Text(
                    text = "Seleziona una quantità",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.padding(top = 20.dp))
        }

        Button(
            onClick = {
                scope.launch {
                    if (team.isEmpty()) {
                        teamError = true
                        return@launch
                    }
                    if (action.isEmpty()) {
                        actionError = true
                        return@launch
                    }
                    if (itemName.isEmpty()) {
                        itemError = true
                        return@launch
                    }
                    if (quantity == 0) {
                        quantityError = true
                        return@launch
                    }
                    try {
                        Log.d("Credits saved", "Sending credits to server")
                        /*Toast.makeText(context, "Invio richiesta con: ${market.type}, ${market.teamId}, ${market.itemId}, ${market.quantity}", Toast.LENGTH_LONG).show()
                        market = MarketAction()
                        item = ItemStats()
                        quantity = 0
                        selectedTeam = null
                        team = ""
                        action = ""
                        itemName = ""*/

                        val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/market") {
                            contentType(ContentType.Application.Json)
                            setBody(market)
                        }
                        if (response.status == HttpStatusCode.OK) {
                            Toast.makeText(context, "Operazione avvenuta con successo", Toast.LENGTH_LONG).show()
                            Log.d("Operation executed", "operation executed correctly")
                            market = MarketAction()
                            item = ItemStats()
                            quantity = 0
                            selectedTeam = null
                            team = ""
                            action = ""
                            itemName = ""
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Can't connect to server", Toast.LENGTH_SHORT).show()
                        Log.e("Network error", "Errore nella post: ${e.localizedMessage}")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryColor,
                contentColor = PrimaryColor,
                disabledContainerColor = SecondaryColor.copy(alpha = 0.5f),
                disabledContentColor = PrimaryColor.copy(alpha = 0.5f)
            ),
            enabled = market.isValid,
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(text = "${market.type}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}