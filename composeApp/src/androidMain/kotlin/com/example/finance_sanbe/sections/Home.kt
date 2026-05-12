package com.example.finance_sanbe.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finance_sanbe.BackgroundColor
import com.example.finance_sanbe.PrimaryColor

@Composable
fun Home() {
    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Benvenuto nel marketplace di San Bernardo", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}