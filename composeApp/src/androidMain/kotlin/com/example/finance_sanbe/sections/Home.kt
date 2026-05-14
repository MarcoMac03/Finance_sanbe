package com.example.finance_sanbe.sections

import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.InnerShadowScope
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.InnerShadowPainter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var itemStats by remember { mutableStateOf<List<ItemStats>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(itemStats) {
        isLoading = true
        try {
            itemStats = NetworkClient.client.get("${NetworkClient.BASE_URL}/itemsAll").body()
            Toast.makeText(context, "Lista aggiornata", Toast.LENGTH_SHORT).show()
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
        Card(Modifier.padding(18.dp).fillMaxWidth()
            .border(border = BorderStroke(0.dp, color = BackgroundColor), shape = AbsoluteRoundedCornerShape(15.dp)),
            colors = CardDefaults.cardColors(containerColor = SecondaryColor)) {
            Text(text = "Benvenuto nel marketplace di San Bernardo",
                color = PrimaryColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
            )
        }
        Box(Modifier.padding(10.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = "Lista degli articoli", color = PrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.padding(10.dp))
        if(itemStats.isNotEmpty()){
            itemStats.forEach { item ->
                Text(text = item.name, color = PrimaryColor,fontSize = 16.sp)
            }
        } else {
            Text(text = "Nessun articolo disponibile", color = PrimaryColor, fontSize = 16.sp)
        }

    }
}