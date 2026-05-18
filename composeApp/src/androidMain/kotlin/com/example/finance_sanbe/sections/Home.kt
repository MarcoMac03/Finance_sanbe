package com.example.finance_sanbe.sections

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finance_sanbe.BackgroundColor
import com.example.finance_sanbe.ItemStats
import com.example.finance_sanbe.NetworkClient
import com.example.finance_sanbe.PrimaryColor
import com.example.finance_sanbe.SecondaryColor
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun Home() {
    var itemStats by remember { mutableStateOf<List<ItemStats>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var trigger by remember { mutableIntStateOf(0) }
    // quando devo far aggiornare la lista passo nel componente figlio una funzione che aggiorna trigger
    // Figlio(onAddItem -> {trigger ++})

    LaunchedEffect(trigger) {
        isLoading = true
        try {
            itemStats = NetworkClient.client.get("${NetworkClient.BASE_URL}/itemsAll").body()
        } catch(e: Exception) {
            if(e !is CancellationException) {
                Log.e("Network error", "Errore ricerca: ${e.message}", e)
            }
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(20.dp, end = 20.dp, top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            Modifier.padding(18.dp).fillMaxWidth()
            .border(border = BorderStroke(0.dp, color = BackgroundColor), shape = AbsoluteRoundedCornerShape(15.dp)),
            colors = CardDefaults.cardColors(containerColor = SecondaryColor)
        ) {
            Text(text = "Benvenuti nel marketplace di San Bernardo",
                color = PrimaryColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp,
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
            )
        }
        Spacer(modifier = Modifier.padding(30.dp))

        Box(Modifier.padding(10.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = "Lista degli articoli", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.padding(10.dp))
        if (isLoading) {
            Text(text = "Caricamento in corso...", color = PrimaryColor, fontSize = 16.sp)
        } else if (itemStats.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp, start = 30.dp, end = 30.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Oggetto", modifier = Modifier.weight(2f), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        Text(text = "Quantità", modifier = Modifier.weight(2f), fontSize = 18.sp, textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                    HorizontalDivider(thickness = 1.dp, color = PrimaryColor)
                }

                items(itemStats) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.name, modifier = Modifier.weight(1f), fontSize = 16.sp, color = PrimaryColor)
                        Text(text = "${item.actualQuantity}", modifier = Modifier.weight(1f), fontSize = 16.sp, textAlign = TextAlign.End, color = PrimaryColor)
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = PrimaryColor.copy(alpha = 0.5f))
                }
            }
        } else {
            Text(text = "Nessun articolo disponibile", color = PrimaryColor, fontSize = 16.sp)
        }
    }
}