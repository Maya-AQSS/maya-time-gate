package com.example.mayatimegate.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mayatimegate.R
import kotlinx.coroutines.delay

@Composable
fun ConnectionErrorView(
    onTimeOver: () -> Unit,
    navController: NavHostController,
) {

    // Temporizador de visualizacion
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeOver()
    }

    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ConnectionErrorCompose(
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
 * Contenedor principal de la vista de error
 */
@Composable
fun ConnectionErrorCompose(modifier: Modifier, onClick: () -> Unit){
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
            // Logotipo del ceed
            Image(
                painter = painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Logo CEEDCV",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            ConnectionErrorText() // Mensaje de error de conecion

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
 * Contenedor con el mensaje de error
 */
@Composable
fun ConnectionErrorText(){
    Image(
        painter = painterResource(R.drawable.ic_wifi_off),
        contentDescription = "Icono Error conexion",
        modifier = Modifier.size(200.dp)
    )
    Spacer(Modifier.size(24.dp))
    Text(
        text = "Error de conexión",
        color = Color(0xFFDD4150),
        style = MaterialTheme.typography.displaySmall
    )
}
