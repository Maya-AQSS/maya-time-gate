package com.example.mayatimegate.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mayatimegate.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import coil.compose.AsyncImage
import com.example.mayatimegate.model.EmployeeResponse
import com.example.mayatimegate.viewmodel.EmployeeViewModel

/**
 * Vista de Éxito: Se muestra tras una identificación correcta.
 * Gestiona el cierre automático para retornar al estado inicial.
 */

@Composable
fun ConfirmationView(
    onTimeOver: () -> Unit,
    viewModel: EmployeeViewModel,
) {

    val employee by viewModel.employeeInfo.observeAsState()


    // Temporizador de visualización: 1.5 segundos son ideales para un feedback rápido
    LaunchedEffect(Unit) {
       delay(30000)
        viewModel.resetData()
        delay(100)
        onTimeOver()
    }

    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        if (employee != null) {
            ConfirmationCompose(
                employee = employee!!,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
           //Mientras cargan los datos o no los encuentra
                LoadingCompose()
        }
    }
}

/**
 * Maquetación de la tarjeta de confirmación.
 */
@Composable
fun ConfirmationCompose(employee: EmployeeResponse, modifier: Modifier) {

    val userName = "${employee.name} ${employee.surname}"
    Card(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image( //logo del ceed
                painter = painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Logo CEEDCV",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            //imagen circular
            CircleImage(userName, employee.imageUrl)

            Spacer(modifier = Modifier.height(40.dp))

            // Mostramos el texto de éxito con el nombre real
            InformationalText(userName)


        }
    }
}

/**
 * Componente para mostrar la imagen del usuario en formato circular.
 */
@Composable
fun CircleImage(userName: String, url: String?) {

    val icon = userName.split(" ")
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
        if (!url.isNullOrEmpty()) { //Si hay una foto se muestra
            AsyncImage(
                model = url,
                contentDescription = "Foto de perfil",
                placeholder = painterResource(R.drawable.logo_ceedcv), // Una imagen gris o logo
                error = painterResource(R.drawable.ic_error), // Una imagen de aviso
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
                        text = icon,
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
fun InformationalText(user:String) {
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
            text = "Bienvenid@/Adios, $user",
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

@Composable
fun LoadingCompose(){ //Vista mientras se cargan los datos
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            "Cargando...",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(50.dp))
        CircularProgressIndicator( //Circulo de progreso

        )
    }
}

