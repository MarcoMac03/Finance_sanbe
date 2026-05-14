package com.example.finance_sanbe.sections

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finance_sanbe.BackgroundColor
import com.example.finance_sanbe.NewItem
import com.example.finance_sanbe.PrimaryColor
import com.example.finance_sanbe.SecondaryColor
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import com.example.finance_sanbe.NetworkClient

@Composable
fun AddItem() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var item by remember { mutableStateOf(NewItem()) }
    var quantityError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(16.dp),
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

        Text(text = "Inserisci nome oggetto", color = PrimaryColor, fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(top = 20.dp))

            OutlinedTextField (
                value = item.name.lowercase(),
                onValueChange = { item = item.copy(name = it)},
                shape = RoundedCornerShape(12.dp),
                label = { Text("Oggetto", color = PrimaryColor, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SecondaryColor,
                    unfocusedContainerColor = SecondaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = PrimaryColor,
                    unfocusedLabelColor = PrimaryColor,
                    focusedTextColor = PrimaryColor,
                    unfocusedTextColor = PrimaryColor,
                ),
            )

        AnimatedVisibility(visible = nameError) {
            Text(
                text = "Inserisci un nome",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 30.dp))

        Text(text = "Inserisci prezzo", color = PrimaryColor, fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(top = 20.dp))

            OutlinedTextField (
                value = item.price.toString(),
                onValueChange = {
                    val newValue = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                    item = item.copy(price = newValue)
                },
                shape = RoundedCornerShape(12.dp),
                label = { Text("Prezzo", color = PrimaryColor, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SecondaryColor,
                    unfocusedContainerColor = SecondaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = PrimaryColor,
                    unfocusedLabelColor = PrimaryColor,
                    focusedTextColor = PrimaryColor,
                    unfocusedTextColor = PrimaryColor,
                ),
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

        Text(text = "Inserisci quantità", color = PrimaryColor, fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(top = 20.dp))

        OutlinedTextField (
            value = item.quantity.toString(),
            onValueChange = {
                val newValue = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                item = item.copy(quantity = newValue) },
            shape = RoundedCornerShape(12.dp),
            label = { Text("Quantità", fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
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

        AnimatedVisibility(visible = quantityError) {
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
                        if (item.quantity <= 0) {
                            quantityError = true
                            return@launch
                        }
                        if(item.price < 0){
                            priceError = true
                            return@launch
                        }
                        if (item.name.isBlank()) {
                            nameError = true
                            return@launch
                        }
                        try {
                            Log.d("Item save", "Sending item to server")
                            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/item") {
                                contentType(ContentType.Application.Json)
                                setBody(item)
                            }
                            if (response.status == HttpStatusCode.Created) {
                                Toast.makeText(context, "Oggetto aggiunto", Toast.LENGTH_LONG).show()
                                Log.d("Item save", "Item added correctly")
                                item = NewItem()
                            }
                        } catch(e: Exception) {
                            Toast.makeText(context, "Can't connect to server", Toast.LENGTH_SHORT).show()
                            Log.e("Network error", "Errore nella post: ${e.localizedMessage}")
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
            enabled = item.quantity > 0 && item.price >= 0 && item.name.isNotBlank(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(text = "Salva", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}