package com.example.mayatimegate.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mayatimegate.R
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ErrorView(navController: NavHostController, onTimeOver: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(4000)
        onTimeOver()
    }
    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ErrorCompose(
            modifier = Modifier.padding(innerPadding),
            onClick = {
                navController.navigate("login") {
                    popUpTo("login") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
fun ErrorCompose(modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFDFDFD),
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Imagen tarjeta",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            ErrorText()
            Spacer(modifier = Modifier.height(80.dp))
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.padding(16.dp),
                border = BorderStroke(3.dp, Color(0xFFDD4150)),
            ) {
                Image(
                    painterResource(R.drawable.ic_replay),
                    contentDescription = "Reintentar",
                    modifier = Modifier.size(35.dp)
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    "Reintentar",
                    color = Color(0xFFDD4150),
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}

@Composable
fun ErrorText(){
    val currentTime = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            "¡Identificación no válida!",
            color = Color(0xFFDD4150),
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            "No se reconoce el DNI o la tarjeta",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.size(42.dp))
        Text(
            "Por favor, intenta registrarte de nuevo y si el error persiste, pase por Soporte/Dirección",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

    }
}


