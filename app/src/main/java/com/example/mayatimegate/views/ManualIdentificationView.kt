package com.example.mayatimegate.views

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mayatimegate.R

/**
 * Vista de Identificación Manual.
 * Permite al usuario fichar introduciendo DNI y contraseña si no dispone de tarjeta.
 */
@Composable
fun ManualIdentificationView(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ManualIdentificationCompose(
            modifier = Modifier.padding(innerPadding),
            onBackClick = {
                // Navegación segura hacia atrás comprobando la pila
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            },
            onManualClick = {
                navController.navigate("confirmation") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

/**
 * Orquestador de la lógica de formulario y validación.
 */
@Composable
fun ManualIdentificationCompose(modifier: Modifier, onBackClick: () -> Unit, onManualClick: () -> Unit) {
    // Estados para almacenar los valores de entrada
    var dni by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    // Estados para controlar la visualización de errores
    var dniIsError by remember { mutableStateOf(false) }
    var passIsError by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxSize().padding(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
    ) {
        BackRow(onBackClick = onBackClick)

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
        ) {
            TitleScreen()

            Spacer(modifier = Modifier.height(60.dp))

            Text("Documento de identidad (DNI/NIE)", style = MaterialTheme.typography.titleLarge)
            IdentityTextField(
                isError = dniIsError,
                onValueReady = { value ->
                    dni = value
                    // Limpieza dinámica del error si el usuario corrige el dato
                    if (dniIsError && value.length >= 8) dniIsError = false
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text("Contraseña", style = MaterialTheme.typography.titleLarge)
            PassTextField(
                isError = passIsError,
                onValueReady = { value ->
                    pass = value
                    if (passIsError && value.isNotEmpty()) passIsError = false
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            RegisterButton(
                onClick = {
                    // Lógica de validación antes de proceder al registro
                    if (dni.length >= 8 && pass.isNotEmpty()) {
                        onManualClick()
                    } else {
                        // Activación de estados de error para feedback visual
                        dniIsError = dni.length < 8
                        passIsError = pass.isEmpty()
                    }
                }
            )
        }
    }
}

/**
 * Botón de retroceso personalizado con área de clic extendida.
 */
@Composable
fun BackRow(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onBackClick() }
            .fillMaxWidth()
            .padding(25.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "Volver",
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text("Volver", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun TitleScreen() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Identificación Manual",
            color = Color(0xFF0D6DFB),
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "Introduce tus credenciales para fichar",
            style = MaterialTheme.typography.displaySmall
        )
    }
}

/**
 * Campo de texto especializado para DNI con teclado numérico.
 */
@Composable
fun IdentityTextField(isError: Boolean, onValueReady: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }

    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            onValueReady(it)
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        placeholder = { Text("Ej: 12345678", color = Color.Gray) },
        leadingIcon = {
            Icon(painterResource(R.drawable.ic_badge), null, Modifier.size(35.dp))
        },
        trailingIcon = {
            // Prioridad: Icono de borrar si hay texto, si no, icono de error
            if (textState.isNotEmpty()) {
                IconButton(onClick = {
                    textState = ""
                    onValueReady("")
                }) {
                    Icon(Icons.Default.Clear, "Borrar")
                }
            } else if (isError) {
                Icon(Icons.Default.Info, "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        isError = isError,
        supportingText = {
            if (isError) Text("El DNI debe tener al menos 8 números")
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            errorTextColor = Color.Black, // Mantiene el color del texto aunque haya error
            errorBorderColor = Color.Red
        ),
        singleLine = true
    )
}

/**
 * Campo de texto para contraseña con transformación visual de seguridad.
 */
@Composable
fun PassTextField(isError: Boolean, onValueReady: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }

    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            onValueReady(it)
        },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        placeholder = { Text("Tu contraseña de Odoo", color = Color.Gray) },
        leadingIcon = {
            Icon(painterResource(R.drawable.ic_lock), null, Modifier.size(35.dp))
        },
        trailingIcon = {
            if (textState.isNotEmpty()) {
                IconButton(onClick = {
                    textState = ""
                    onValueReady("")
                }) {
                    Icon(Icons.Default.Clear, "Borrar")
                }
            } else if (isError) {
                Icon(Icons.Default.Info, "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        isError = isError,
        supportingText = {
            if (isError) Text("Introduce tu contraseña")
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            errorTextColor = Color.Black,
            errorBorderColor = Color.Red
        ),
        singleLine = true
    )
}

/**
 * Botón de confirmación de registro.
 */
@Composable
fun RegisterButton(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF34495E)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(R.drawable.ic_register), null, Modifier.size(25.dp))
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = "Registrar Fichaje",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}