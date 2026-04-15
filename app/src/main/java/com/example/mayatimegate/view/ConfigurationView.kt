package com.example.mayatimegate.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.mayatimegate.R
import com.example.mayatimegate.viewmodel.SettingsViewModel

/**
 * Vista de configuracion de la aplicacion
 */
@Composable
fun ConfigurationView(navController: NavHostController, viewModel: SettingsViewModel) {

    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ConfigurationCompose(
            modifier = Modifier.padding(innerPadding),
            onBackClick = {
                // Navegación segura hacia atrás comprobando la pila
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            },
            viewModel
        )
    }
}

@Composable
fun ConfigurationCompose(modifier: Modifier, onBackClick: () -> Unit, viewModel: SettingsViewModel){
    val focusManager = LocalFocusManager.current
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // Esto quita el cursor y cierra el teclado
                })
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        BackRow(onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ){
            ConfigurationTitle()
            Spacer(Modifier.size(40.dp))
            DeviceName(viewModel, focusManager)
            Spacer(Modifier.size(20.dp))
            OdooURL(viewModel, focusManager)
        }

    }
}

@Composable
fun ConfigurationTitle(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(Modifier.size(12.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = "Abrir configuracion",
            Modifier.size(35.dp)
        )
    }
}

@Composable
fun DeviceName(viewModel: SettingsViewModel, focusManager: FocusManager){
    // Control de Nombre del Dispositivo
    val deviceNameSaved by viewModel.deviceName.collectAsState()

    var localName by remember { mutableStateOf("") }

    // Sincronizamos el estado local cuando el guardado cambie
    LaunchedEffect(deviceNameSaved) {
        localName = deviceNameSaved
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // Esto quita el cursor y cierra el teclado
                })
            },

        value = localName,
        onValueChange = {
            localName = it
            viewModel.updateDeviceName(it)
        },

        label = { Text("Nombre del dispositivo") },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = Color(0xFF0D6DFB),
            unfocusedLabelColor = Color.DarkGray,
            focusedBorderColor = Color(0xFF0D6DFB),
            unfocusedBorderColor = Color.DarkGray
        ),
        singleLine = true
    )
}

@Composable
fun OdooURL(viewModel: SettingsViewModel, focusManager: FocusManager){
    // Control de Nombre del Dispositivo
    val UrlSaved by viewModel.urlName.collectAsState()

    var localUrl by remember { mutableStateOf("") }

    // Sincronizamos el estado local cuando el guardado cambie
    LaunchedEffect(UrlSaved) {
        localUrl = UrlSaved
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = localUrl,
        onValueChange = {
            localUrl = it
            viewModel.updateUrlName(it)
        },
        label = { Text("URL de Odoo") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = Color(0xFF0D6DFB),
            unfocusedLabelColor = Color.DarkGray,
            focusedBorderColor = Color(0xFF0D6DFB),
            unfocusedBorderColor = Color.DarkGray
        ),
        singleLine = true

    )
}
