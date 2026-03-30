package com.example.mayatimegate.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mayatimegate.R
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ConfirmationView(navController: NavHostController, onTimeOver: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onTimeOver()
    }
    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ConfirmationCompose(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ConfirmationCompose(modifier: Modifier) {
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
            Spacer(modifier = Modifier.height(10.dp))
            CircleImage()
            Spacer(modifier = Modifier.height(40.dp))
            InformationalText()
        }
    }
}

@Composable
fun CircleImage() {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_background),
        contentDescription = "Descripción de la imagen",
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun InformationalText(){
    val currentTime = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painterResource(R.drawable.ic_check_circle),
                contentDescription = "Volver",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                "¡Fichaje Realizado!",
                color = Color(0xFF198654),
                style = MaterialTheme.typography.displayLarge
            )
        }
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            "Bienvenid@, <NOMBRE USUARIO>",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.size(42.dp))
        Text(
            "Hora de entrada: $currentTime",
            style = MaterialTheme.typography.titleLarge
        )
        Text("")
    }
}


