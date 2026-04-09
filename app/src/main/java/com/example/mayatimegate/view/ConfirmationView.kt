package com.example.mayatimegate.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mayatimegate.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import coil.compose.AsyncImage

/**
 * Vista de Éxito: Se muestra tras una identificación correcta.
 * Gestiona el cierre automático para retornar al estado inicial.
 */

class User( //Clase de prueba
    val name: String,
    val url: String?,
)

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

    val user = User("Santi Selva", "https://avatars.githubusercontent.com/u/1?v=4")

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
            CircleImage(user)

            Spacer(modifier = Modifier.height(40.dp))

            InformationalText(user)
        }
    }
}

/**
 * Componente para mostrar la imagen del usuario en formato circular.
 */
@Composable
fun CircleImage(user:User) {

    val iniciales = user.name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    Box( //Caja que representa el borde verde de la imagen
        modifier = Modifier
            .size(200.dp)
            .border(
                width = 6.dp,
                color = Color(0xFF4CAF50),
                shape = CircleShape
            )
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!user.url.isNullOrEmpty()) { //Si hay una foto se muestra
            AsyncImage(
                model = user.url,
                contentDescription = "Foto de perfil",
                placeholder = painterResource(R.drawable.logo_ceedcv), // Una imagen gris o logo
                error = painterResource(R.drawable.ic_replay), // Una imagen de aviso
                onLoading = { println("Coil: Cargando...") },
                onError = { error -> println("Coil error: ${error.result.throwable}") },
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else { // Si no hay foto se muestra sus iniciales
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF4CAF50)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = iniciales,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        }
    }
}
/**
 * Bloque de texto con el resumen de la operación realizada.
 */
@Composable
fun InformationalText(user:User) {
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
                color = Color(0xFF198654), // Verde estándar de éxito
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        // Saludo personalizado 
        Text(
            text = "Bienvenid@/Adios, ${user.name}",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.size(42.dp))

        // Confirmación visual de la hora registrada
        Text(
            text = "Hora de entrada/salida: $currentTime",
            style = MaterialTheme.typography.titleLarge
        )
    }
}