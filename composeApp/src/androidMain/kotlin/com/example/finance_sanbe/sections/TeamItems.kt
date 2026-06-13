package com.example.finance_sanbe.sections

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finance_sanbe.AddCredits
import com.example.finance_sanbe.BackgroundColor
import com.example.finance_sanbe.ItemStats
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
fun TeamItems() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var trigger by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var teamList by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var team by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<ItemStats>>(emptyList()) }
    var teamCredits by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingItems by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<Int?>(null) }
    var addCredits by remember { mutableStateOf(AddCredits()) }

    LaunchedEffect(expanded) {
        if(expanded && teamList.isEmpty()) {
            isLoading = true

            try {
                teamList = NetworkClient.client.get("${NetworkClient.BASE_URL}/teams").body()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTeam, trigger) {

        selectedTeam?.let { id ->
            isLoadingItems = true
            Log.d("Team selected", "Team selected: $id")
            try{
                items = NetworkClient.client.get("${NetworkClient.BASE_URL}/teamItems?id=${selectedTeam}").body()
                teamCredits = NetworkClient.client.get("${NetworkClient.BASE_URL}/teamCredits?id=${selectedTeam}").body()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            } finally {
                isLoadingItems = false
            }
        }
    }
    Log.e("TEST", "Problema con Log.d")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(top = 30.dp, bottom = 20.dp, start = 30.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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

                    Text(text = "Seleziona Squadra", color = PrimaryColor, fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(top = 20.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = team,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Squadra", color = PrimaryColor, fontSize = 14.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
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
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.heightIn(max = 200.dp),
                        ) {
                            teamList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption.second) },
                                    onClick = {
                                        team = selectionOption.second
                                        selectedTeam = selectionOption.first
                                        addCredits = addCredits.copy(teamId = selectionOption.first)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if(selectedTeam != null) {
            item {
                Box(Modifier.padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 30.dp).background(BackgroundColor).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Crediti rimanenti: $teamCredits", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Box(Modifier
                .padding(10.dp)
                .fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Oggetti della squadra", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.padding(10.dp))
        }

        if(isLoadingItems) {
            item {
                Text(text = "Caricamento in corso...", color = PrimaryColor, fontSize = 16.sp)
            }
        } else if (items.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Oggetto", modifier = Modifier.weight(2f), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        Text(text = "Quantità", modifier = Modifier.weight(2f), fontSize = 18.sp, textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                    HorizontalDivider(thickness = 1.dp, color = PrimaryColor)
                }

                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 30.dp, end = 30.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.name, modifier = Modifier.weight(1f), fontSize = 16.sp, color = PrimaryColor)
                        Text(text = "${item.actualQuantity}", modifier = Modifier.weight(1f), fontSize = 16.sp, textAlign = TextAlign.End, color = PrimaryColor)
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = PrimaryColor.copy(alpha = 0.5f))
                }
        } else {
            item {
                Text(text = "Nessun articolo disponibile", color = PrimaryColor, fontSize = 16.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.padding(top = 30.dp))

            Text(text = "Aggiungi crediti", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.padding(top = 30.dp))

            OutlinedTextField(
                value = addCredits.credits.toString(),
                onValueChange = {
                    val newValue = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                    addCredits = addCredits.copy(credits = newValue) },
                shape = RoundedCornerShape(12.dp),
                label = { Text("Crediti", color = PrimaryColor, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SecondaryColor,
                    unfocusedContainerColor = SecondaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = PrimaryColor,
                    unfocusedLabelColor = PrimaryColor,
                    focusedTextColor = PrimaryColor,
                    unfocusedTextColor = PrimaryColor
                )
            )
            AnimatedVisibility(visible = priceError) {
                Text(
                    text = "Valore minore di 0 non valido",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.padding(top = 30.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (addCredits.credits <= 0) {
                            priceError = true
                            return@launch
                        }
                        try {
                            Log.d("Credits saved", "Sending credits to server")
                            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/addCredits") {
                                contentType(ContentType.Application.Json)
                                setBody(addCredits)
                            }
                            if (response.status == HttpStatusCode.OK) {
                                Toast.makeText(context, "Crediti aggiunti", Toast.LENGTH_LONG).show()
                                Log.d("Credits saved", "Credits added correctly")
                                addCredits = AddCredits()
                                trigger = !trigger
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
                enabled = addCredits.credits > 0 && (selectedTeam != null),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = "Aggiungi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}