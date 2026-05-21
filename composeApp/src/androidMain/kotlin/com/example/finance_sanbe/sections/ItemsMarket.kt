package com.example.finance_sanbe.sections

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.finance_sanbe.AddCredits
import com.example.finance_sanbe.BackgroundColor
import com.example.finance_sanbe.ItemStats
import com.example.finance_sanbe.MarketAction
import com.example.finance_sanbe.MarketStats
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
    var item by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var teamList by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var itemList by remember { mutableStateOf<List<ItemStats>>(emptyList()) }
    var team by remember { mutableStateOf("") }
    var selectedTeam by remember { mutableStateOf<Int?>(null) }
    var market by remember { mutableStateOf(MarketAction()) }
    var itemPQ by remember { mutableStateOf(MarketStats())}
    var quantity by remember { mutableIntStateOf(0) }

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

    LaunchedEffect(expandedItems) {
        if(expandedItems && itemList.isEmpty()) {
            try {
                itemList = NetworkClient.client.get("${NetworkClient.BASE_URL}/itemsAll").body()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            }
        }
    }

    LaunchedEffect(item) {
        if(!item.isEmpty() && !action.isEmpty() && selectedTeam != null) {
            try {
                itemPQ = NetworkClient.client.get("${NetworkClient.BASE_URL}/itemStats/${market.itemId}/${market.type}/${selectedTeam}").body()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            }
        }
    }

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
                            market = market.copy(teamId = selectionOption.first)
                            expandedTeam = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))

        // selezione item
        Text(text = "Seleziona oggetto", color = PrimaryColor, fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(top = 10.dp))

        ExposedDropdownMenuBox(
            expanded = expandedItems,
            onExpandedChange = { expandedItems = it }
        ) {
            OutlinedTextField(
                value = item,
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
                            item = selectionOption.name
                            market = market.copy(itemId = selectionOption.id)
                            expandedItems = false
                        }
                    )
                }
            }
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
                            market = market.copy(type = selectionOption)
                            expandedActions = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))

        if(!item.isEmpty() && !action.isEmpty() && selectedTeam != null) {
            Box(Modifier
                .padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 30.dp)
                .background(BackgroundColor)
                .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Prezzo: ${itemPQ.price * itemPQ.quantity}", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Quantità", color = PrimaryColor, fontSize = 14.sp) },
                    trailingIcon = {
                        Row(Modifier.padding(end = 4.dp)) {
                            IconButton(
                                onClick = {
                                    if(quantity > 0) {
                                        quantity--
                                        market = market.copy(quantity = quantity)
                                    }
                                }
                            ) { Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Diminuisci", tint = if(quantity > 1) PrimaryColor else Color.Gray) }
                            IconButton(
                                onClick = {
                                    if(quantity<itemPQ.quantity) {
                                        quantity++
                                        market = market.copy(quantity = quantity)
                                    }
                                }
                            ) { Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Aumenta", tint = PrimaryColor) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = PrimaryColor,
                        unfocusedTextColor = PrimaryColor,
                    )
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    /*if (addCredits.credits <= 0) {
                        priceError = true
                        return@launch
                    }*/
                    try {
                        Log.d("Credits saved", "Sending credits to server")
                        Toast.makeText(context, "Invio richiesta con: ${market.type}, ${market.teamId}, ${market.itemId}, ${market.quantity}", Toast.LENGTH_LONG).show()
                        /*val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/addCredits") {
                            contentType(ContentType.Application.Json)
                            setBody(addCredits)
                        }
                        if (response.status == HttpStatusCode.OK) {
                            Toast.makeText(context, "Crediti aggiunti", Toast.LENGTH_LONG).show()
                            Log.d("Credits saved", "Credits added correctly")
                            market = MarketAction()
                            trigger = !trigger
                        }*/
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
            //enabled = addCredits.credits > 0 && (selectedTeam != null),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(text = "${market.type}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}