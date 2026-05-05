package com.example.mayatimegate.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mayatimegate.R
import com.example.mayatimegate.utils.SoundManager
import com.example.mayatimegate.viewmodel.EmployeeViewModel
import kotlinx.coroutines.delay

@Composable
fun DoubleSigninAlertView(
    onTimeOver: () -> Unit,
    navController: NavHostController,
    soundManager: SoundManager,
    viewModel: EmployeeViewModel,
) {

    val timeout = 9000L

    // Temporizador de visualizacion
    LaunchedEffect(Unit) {
        delay(200)
        soundManager.play("error") //lanzamos sonido de error
        delay(timeout)
        onTimeOver()
    }

    Scaffold(
        containerColor = Color(0xFFEEECEB),
    ) { innerPadding ->
        DoubleSigninCompose(

            modifier = Modifier.padding(innerPadding),

            // Navegacion manual
            onBackClick = {
                navController.navigate("login") { // navega a la pantalla de login
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            },
            onSigningClick = {
                viewModel.onSigningConfirmed()
                navController.navigate("confirmation") { // navegar a la pantalla de confirmacion
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

/**
 * Contenedor principal de la vista de alerta
 */
@Composable
fun DoubleSigninCompose(modifier: Modifier, onBackClick: () -> Unit, onSigningClick: ()-> Unit){
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

            Spacer(modifier = Modifier.height(20.dp))

            DoubleSigninText() // Mensaje de error de conecion

            Spacer(modifier = Modifier.height(60.dp))

            AlertButton(onSigningClick, "Volver a fichar", R.drawable.ic_replay)
            Spacer(modifier = Modifier.height(30.dp))
            AlertButton(onBackClick, "Cancelar", R.drawable.ic_cancel)

        }
    }
}

/**
 * Boton reciclable
 */

@Composable
fun AlertButton(onClick: () -> Unit, text: String, icon: Int) {
    // Botón de acción para reintentar el proceso manualmente
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth() // Que ocupe más espacio horizontal para facilitar el toque
            .height(60.dp),
        border = BorderStroke(2.dp, Color(0xFFF4D03F)),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF4D03F))
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/**
 * Contenedor con el mensaje de alerta
 */
@Composable
fun DoubleSigninText() {
    // Usamos un contenedor para darle dinamismo al icono
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(220.dp)
    ) {
        // Un círculo de fondo amarillo
        Surface(
            shape = CircleShape,
            color = Color(0xFFF4D03F).copy(alpha = 0.1f),
            modifier = Modifier.fillMaxSize()
        ) {}

        Image(
            painter = painterResource(R.drawable.outline_timer_24),
            contentDescription = "Icono Doble Fichaje",
            modifier = Modifier.size(140.dp),
            // Si el recurso es un vector, puedes tintarlo dinámicamente
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFF4D03F))
        )
    }

    Spacer(Modifier.height(32.dp))

    Text(
        text = "¿Ya pasaron 5 minutos?",
        color = Color(0xFF2D2D2D), // Texto oscuro para legibilidad
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(12.dp))

    Surface(
        color = Color(0xFFF4D03F).copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Fichaste hace menos de 5 minutos",
            color = Color(0xFF856404), // Un tono mostaza oscuro para contraste
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}


