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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mayatimegate.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import coil.compose.AsyncImage
import com.example.mayatimegate.model.EmployeeResponse
import com.example.mayatimegate.viewmodel.EmployeeViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.mayatimegate.utils.SoundManager

/**
 * Vista de Éxito: Se muestra tras una identificación correcta.
 * Gestiona el cierre automático para retornar al estado inicial.
 */

@Composable
fun ConfirmationView(
    onTimeOver: () -> Unit,
    viewModel: EmployeeViewModel,
    navController: NavHostController,
    soundManager: SoundManager
) {

    // Observamos el estado del empleado desde el ViewModel
    val employee by viewModel.employeeInfo.observeAsState()

    // Obtenemos el contexto de Android (necesario para SoundPool)
    //val context = LocalContext.current

    // Creamos el SoundManager UNA sola vez (gracias a remember)
    //val soundManager = remember { SoundManager(context) }

    // Este bloque se ejecuta cuando cambia el estado (success / error)
    LaunchedEffect(employee?.status) {

        println("DEBUG: El estado del empleado es: $employee")

        // Solo actuamos si hay datos
        if (employee != null) {

            when (employee?.status) {

                "success" -> {
                    // Reproducimos sonido dependiendo si entra o sale
                    if (employee!!.isSigned) {
                        soundManager.play("success-in")
                    } else {
                        soundManager.play("success-out")
                    }

                    // Esperamos 2 segundos antes de salir de la pantalla
                    delay(3000)

                    // Avisamos al padre para cambiar de pantalla
                    onTimeOver()
                }

                "error" -> {

                    // Navegación según tipo de error
                    if (employee!!.message == "connection-error") {
                        navController.navigate("connection_error")
                    } else {
                        navController.navigate("error")
                    }
                }
            }

            // Reseteamos el estado en el ViewModel para evitar repeticiones
            viewModel.resetData()
        }
    }

    // UI de la pantalla
    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->

        // Si hay datos del empleado mostramos la confirmación
        if (employee?.name != null) {

            ConfirmationCompose(
                employee = employee!!,
                modifier = Modifier.padding(innerPadding)
            )

        } else {
            // Si no hay datos mostramos loading
            LoadingCompose()
        }
    }
}
/**
 * Maquetación de la tarjeta de confirmación.
 */
@Composable
fun ConfirmationCompose(employee: EmployeeResponse, modifier: Modifier) {

    // Nombre completo del usuario
    val userName = "${employee.name} ${employee.surname}"

    // Tarjeta principal
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo de la empresa
            Image(
                painter = painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Logo CEEDCV",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Imagen circular del usuario
            CircleImage(userName, employee.imageUrl)

            Spacer(modifier = Modifier.height(40.dp))

            // Texto con información del fichaje
            InformationalText(userName, employee.isSigned)
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
fun InformationalText(user: String, signed: Boolean) {
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
        if(signed){
            Text(
                text = "Bienvenid@, $user",
                style = MaterialTheme.typography.displaySmall
            )
        }else{
            Text(
                text = "Adios, $user",
                style = MaterialTheme.typography.displaySmall
            )
        }

        Spacer(modifier = Modifier.size(42.dp))

        // Confirmación visual de la hora registrada
        if(signed){
            Text(
                text = "Hora de entrada: $currentTime",
                style = MaterialTheme.typography.titleLarge
            )
        }else{
            Text(
                text = "Hora de salida: $currentTime",
                style = MaterialTheme.typography.titleLarge
            )
        }
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
        CircularProgressIndicator()//Circulo de progreso
    }
}

