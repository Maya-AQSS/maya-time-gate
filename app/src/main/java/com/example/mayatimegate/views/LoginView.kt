package com.example.mayatimegate.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.res.painterResource
import com.example.mayatimegate.R
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController

/**
 * Punto de entrada principal de la aplicación (Pantalla de Fichaje).
 */
@Composable
fun LoginView(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFFEEECEB) // Fondo neutro para resaltar la tarjeta central
    ) { innerPadding ->
        LoginCompose(
            modifier = Modifier.padding(innerPadding),
            onManualClick = {
                // Navegación hacia el formulario manual manteniendo el estado de la pila
                navController.navigate("manual_id") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

/**
 * Contenedor principal que organiza los elementos visuales de la pantalla de inicio.
 */
@Composable
fun LoginCompose(modifier: Modifier, onManualClick: () -> Unit) {
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
            Image(
                painter = painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Logo Institucional",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CurrentTime() // Visualización de hora en tiempo real

            Spacer(modifier = Modifier.height(15.dp))

            CurrentDate() // Fecha actual formateada

            Spacer(modifier = Modifier.height(40.dp))

            CardOption() // Instrucción visual para el uso de tarjeta NFC/RFID

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 25.dp),
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )

            ManualOption(onClick = onManualClick) // Acceso alternativo por teclado
        }
    }
}

/**
 * Integra un TextClock nativo de Android para asegurar precisión y bajo consumo de recursos.
 */
@Composable
fun CurrentTime() {
    AndroidView(
        factory = { context ->
            android.widget.TextClock(context).apply {
                format12Hour = "HH:mm"
                format24Hour = "HH:mm"
                textSize = 50f
                setTextColor(android.graphics.Color.BLACK)
                gravity = android.view.Gravity.CENTER
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

/**
 * Muestra la fecha del sistema formateada para el ámbito local (España).
 */
@Composable
fun CurrentDate() {
    val date = LocalDate.now()
    // Ejemplo: "lunes, 31 de marzo"
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val currentDate = date.format(formatter)

    Text(
        text = currentDate.replaceFirstChar { it.uppercase() }, // Capitaliza el día de la semana
        style = MaterialTheme.typography.headlineLarge
    )
}

/**
 * Panel informativo que indica al usuario cómo interactuar con el lector físico.
 */
@Composable
fun CardOption() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        border = BorderStroke(2.dp, Color(0xFFE1E5E8)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7F8)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_badge),
                contentDescription = "Icono Tarjeta",
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "Acerca tu tarjeta al lector",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "El sistema registrará tu entrada automáticamente",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
    }
}

/**
 * Botón de acción secundaria para usuarios sin tarjeta física.
 */
@Composable
fun ManualOption(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF34495E)), // Color de contraste oscuro
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_keyboard),
                contentDescription = "Icono Teclado",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = "Identificación Manual (DNI/Clave)",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}