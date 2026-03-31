package com.example.mayatimegate.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mayatimegate.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Vista de Éxito: Se muestra tras una identificación correcta.
 * Gestiona el cierre automático para retornar al estado inicial.
 */
@Composable
fun ConfirmationView(onTimeOver: () -> Unit) {
    // Temporizador de visualización: 1.5 segundos son ideales para un feedback rápido
    LaunchedEffect(Unit) {
        delay(1500)
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

/**
 * Maquetación de la tarjeta de confirmación.
 */
@Composable
fun ConfirmationCompose(modifier: Modifier) {
    Card(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Logo CEEDCV",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Imagen de perfil o estado del usuario (actualmente placeholder)
            CircleImage()

            Spacer(modifier = Modifier.height(40.dp))

            InformationalText()
        }
    }
}

/**
 * Componente para mostrar la imagen del usuario o un avatar en formato circular.
 */
@Composable
fun CircleImage() {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_background), // Idealmente será la foto del empleado
        contentDescription = "Foto de perfil",
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape), // Recorte circular estricto
        contentScale = ContentScale.Crop // Asegura que la imagen llene el círculo sin deformarse
    )
}

/**
 * Bloque de texto con el resumen de la operación realizada.
 */
@Composable
fun InformationalText() {
    // Captura la hora exacta en el momento de la composición
    val currentTime = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_check_circle),
                contentDescription = "Icono éxito",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "¡Fichaje Realizado!",
                color = Color(0xFF198654), // Verde estándar de éxito (Material Success)
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        // Saludo personalizado (espacio reservado para integración con backend)
        Text(
            text = "Bienvenid@, <NOMBRE USUARIO>",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.size(42.dp))

        // Confirmación visual de la hora registrada
        Text(
            text = "Hora de entrada: $currentTime",
            style = MaterialTheme.typography.titleLarge
        )
    }
}