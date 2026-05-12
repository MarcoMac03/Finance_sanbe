package com.example.finance_sanbe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finance_sanbe.sections.*

import finance_sanbe.composeapp.generated.resources.Res
import finance_sanbe.composeapp.generated.resources.compose_multiplatform

enum class Screen { Home, AddItem, TeamItems, ItemsMarket }
val BackgroundColor = Color(0xFF0C073B)
val PrimaryColor = Color(0xFFFFFFFF)
val SecondaryColor = Color(0xFF584F78)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    Scaffold (
        containerColor = BackgroundColor,
        bottomBar = {
            TapBar(currentScreen = currentScreen, onTabClick = { currentScreen = it })
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            when (currentScreen) {
                Screen.Home -> Home()
                Screen.AddItem -> AddItem()
                Screen.ItemsMarket -> ItemsMarket()
                Screen.TeamItems -> TeamItems()
            }
        }
    }
}