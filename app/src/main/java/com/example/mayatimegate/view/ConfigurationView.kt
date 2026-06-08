package com.example.mayatimegate.view

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.mayatimegate.R
import com.example.mayatimegate.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.PrimitiveKind

/**
 * Vista de configuracion de la aplicacion
 */
@Composable
fun ConfigurationView(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    onTimeOver: () -> Unit
) {
    val timeout = 20000L
    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ConfigurationInactivityTimer(
            timeout,
            onTimeout = onTimeOver,
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
/**
 * Contenedor que se encarga de gestionar el cierre de la vista por inactividad
 */
@Composable
fun ConfigurationInactivityTimer(
    timeoutMillis: Long = 10000L,
    onTimeout: () -> Unit,
    modifier: Modifier,
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel
) {
    var interactionCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(interactionCount) { //Cuando pasa el tiempo se cierra
        delay(timeoutMillis)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Usamos awaitPointerEventScope para interceptar cualquier click
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent(PointerEventPass.Initial)
                        interactionCount++
                    }
                }
            }
    ) {
        ConfigurationCompose(
            modifier = modifier,
            onBackClick = onBackClick,
            onActivity = { interactionCount++ },
            viewModel = viewModel
        )
    }
}
/**
 * Compose con los demas composes que muestra la vista de configuracion
 */
@Composable
fun ConfigurationCompose(
    modifier: Modifier,
    onBackClick: () -> Unit,
    onActivity: () -> Unit,
    viewModel: SettingsViewModel
){
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
            // Demas composes
            ConfigurationTitle()
            Spacer(Modifier.size(40.dp))
            AndroidId(viewModel)
            Spacer(Modifier.size(20.dp))
            DeviceName(viewModel, focusManager, onActivity)
            Spacer(Modifier.size(20.dp))
            AdminPass(viewModel, focusManager, onActivity)
            Spacer(Modifier.size(20.dp))
            ApiKey(viewModel, focusManager, onActivity)
            Spacer(Modifier.size(20.dp))
            OdooURL(viewModel, focusManager, onActivity)
            Spacer(Modifier.size(20.dp))

        }

    }
}

/**
 * Titulo de la vista
 */
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
/**
 * Text field que muestra el nombre del dispositivo
 */
@Composable
fun DeviceName(viewModel: SettingsViewModel, focusManager: FocusManager, onActivity: () -> Unit){
    // Control de Nombre del Dispositivo
    val deviceNameSaved by viewModel.deviceName.collectAsState()

    var localName by remember { mutableStateOf("") }

    //Funciones para guardar el texto y optimizar DataStore

    LaunchedEffect(deviceNameSaved) {
        localName = deviceNameSaved
    }

    LaunchedEffect(localName) {
        snapshotFlow { localName }
            .debounce(300)
            .distinctUntilChanged()
            .collect {
                viewModel.updateDeviceName(it)
                onActivity()
            }
    }

    OutlinedTextField( //Textfiel para introducir el nombre del dispositivo
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },

        value = localName,
        onValueChange = {
            localName = it
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

/**
 * Compose que muestra la contraseña del administrador
 */

@Composable
fun AdminPass(viewModel: SettingsViewModel, focusManager: FocusManager, onActivity: () -> Unit){
    // Control de Nombre del Dispositivo
    val adminPassSaved by viewModel.adminPass.collectAsState()

    var localAdminPass by remember { mutableStateOf("") }

    //Funciones para guardar el texto y optimizar DataStore

    LaunchedEffect(adminPassSaved) {
        localAdminPass = adminPassSaved
    }

    LaunchedEffect(localAdminPass) {
        snapshotFlow { localAdminPass }
            .debounce(300)
            .distinctUntilChanged()
            .collect {
                viewModel.updateAdminPass(it)
                onActivity()
            }
    }

    OutlinedTextField( //Textfiel para introducir el nombre del dispositivo
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },

        value = localAdminPass,
        onValueChange = {
            localAdminPass = it
        },

        label = { Text("Contraseña del Administrador") },
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

/**
 * Text field que muestra el nombre del dispositivo
 */
@Composable
fun ApiKey(viewModel: SettingsViewModel, focusManager: FocusManager, onActivity: () -> Unit){
    // Control de Nombre del Dispositivo
    val apiKeySaved by viewModel.apiKey.collectAsState()

    var localApiKey by remember { mutableStateOf("") }

    //Funciones para guardar el texto y optimizar DataStore

    LaunchedEffect(apiKeySaved) {
        localApiKey = apiKeySaved
    }

    LaunchedEffect(localApiKey) {
        snapshotFlow { localApiKey }
            .debounce(300)
            .distinctUntilChanged()
            .collect {
                viewModel.updateApiKey(it)
                onActivity()
            }
    }

    OutlinedTextField( //Textfiel para introducir el nombre del dispositivo
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },

        value = localApiKey,
        onValueChange = {
            localApiKey = it
        },

        label = { Text("Api key") },
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

/**
 * Compose que muestra un textField con la url a odoo
 */
@Composable
fun OdooURL(viewModel: SettingsViewModel, focusManager: FocusManager, onActivity: () -> Unit){
    // Control de Nombre del Dispositivo
    val UrlSaved by viewModel.urlName.collectAsState()

    var localUrl by remember { mutableStateOf("") }

    var showMessage by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Sincronizamos el estado local cuando el guardado cambie
    LaunchedEffect(UrlSaved) {
        localUrl = UrlSaved
    }

    LaunchedEffect(localUrl) {
        snapshotFlow { localUrl }
            .debounce(300)
            .distinctUntilChanged()
            .collect {
                viewModel.updateUrlName(it)
                onActivity()
            }
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        value = localUrl,
        onValueChange = {
            localUrl = it
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
    IconButton(
        onClick = {
            showMessage = true
            scope.launch {
                delay(4000)
                showMessage = false
            }
        }

    ){
        Icon(
            painter = painterResource(id = R.drawable.ic_help),
            contentDescription = "Abrir configuracion",
            tint=Color(0xFF3596D9),
            modifier = Modifier.size(24.dp)
        )
    }

    if (showMessage) {
        Text("URL wifi: 10.42.0.1")
        Text("URL cable: 10.0.2.2")
    }
}
/**
 * Texto que muestra el android id
 */
@SuppressLint("HardwareIds")
@Composable
fun AndroidId(viewModel: SettingsViewModel,){

    val androidId = viewModel.androidId
    Text(
        "Android ID: $androidId",
        color = Color.Black
    )
}

