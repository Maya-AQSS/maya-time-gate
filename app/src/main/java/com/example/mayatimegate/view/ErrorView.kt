package com.example.mayatimegate.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mayatimegate.R
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

/**
 * Vista de error que gestiona el ciclo de vida y la navegación automática.
 */
@Composable
fun ErrorView(navController: NavHostController, onTimeOver: () -> Unit) {
    // Temporizador de seguridad: redirige al login tras 4 segundos de inactividad
    LaunchedEffect(Unit) {
        delay(4000)
        onTimeOver()
    }

    Scaffold(
        containerColor = Color(0xFFEEECEB) // Color de fondo consistente con el branding
    ) { innerPadding ->
        ErrorCompose(
            modifier = Modifier.padding(innerPadding),
            onClick = {
                // Navegación manual: limpia la pila para evitar bucles en la pantalla de error
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}

/**
 * Contenedor principal de la interfaz de error.
 */
@Composable
fun ErrorCompose(modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logotipo institucional
            Image(
                painter = painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Logo CEEDCV",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            ErrorText() // Bloque de mensajes de error

            Spacer(modifier = Modifier.height(80.dp))

            // Botón de acción para reintentar el proceso manualmente
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.padding(16.dp),
                border = BorderStroke(3.dp, Color(0xFFDD4150)), // Borde rojo de alerta
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_replay),
                    contentDescription = "Icono Reintentar",
                    modifier = Modifier.size(35.dp)
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = "Reintentar",
                    color = Color(0xFFDD4150),
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}

/**
 * Componente dedicado a la jerarquía de textos de error.
 */
@Composable
fun ErrorText() {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Identificación no válida!",
            color = Color(0xFFDD4150),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.size(20.dp))

        Text(
            text = "No se reconoce el DNI o la tarjeta",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.size(42.dp))

        Text(
            text = "Por favor, intenta registrarte de nuevo y si el error persiste, pase por Soporte/Dirección",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center // Alineación centrada para mejorar la lectura
        )
    }
}