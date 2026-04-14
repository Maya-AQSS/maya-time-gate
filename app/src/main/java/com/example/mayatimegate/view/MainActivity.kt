package com.example.mayatimegate.view

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mayatimegate.ui.theme.MayaTimeGateTheme
import com.example.mayatimegate.viewmodel.EmployeeViewModel

class MainActivity : ComponentActivity() {
    private val rfidBuffer = StringBuilder()
    // El estado que la UI va a observar
    private var RFIDCode = mutableStateOf("")

    private lateinit var navController: NavHostController

//    private val employeeViewModel: EmployeeViewModel by lazy {
//        ViewModelProvider(this)[EmployeeViewModel::class.java]
//    }
    private val employeeViewModel: EmployeeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el diseño de borde a borde (detrás de las barras de sistema)

        setContent {
            MayaTimeGateTheme {
                // Controlador central para gestionar el historial y cambio de pantallas
                navController = rememberNavController()

                // Definición del grafo de navegación de la aplicación
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {

                    composable("login") {
                        LoginView(
                            navController,
                            viewModel  = employeeViewModel // <-- Esta es la conexión clave
                        )
                    }

                    composable("manual_id") {
                        ManualIdentificationView(
                            onTimeOver = {
                                navController.popBackStack()
                            },

                            navController = navController,
                            viewModel = employeeViewModel
                        )
                    }

                    composable("confirmation") {
                        ConfirmationView(
                            onTimeOver = {
                                // Redirección automática al inicio limpiando el historial previo
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
                                }

                            },
                            viewModel = employeeViewModel // <-- Esta es la conexión clave
                        )
                    }

                    composable("error") {
                        ErrorView(
                            onTimeOver = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            navController = navController,
                            viewModel = employeeViewModel
                        )
                    }
                }
            }
        }
    }
    /**
     * Interceptamos el teclado externo (lector RFID)
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val char = event.unicodeChar.toChar()

        when (keyCode) { //Detectamos que entran caracteres
            KeyEvent.KEYCODE_ENTER -> {
                val code = rfidBuffer.toString().trim()
                if (code.isNotEmpty()) {
                    RFIDCode.value = code
                    signingProcess(code)
                }
                rfidBuffer.setLength(0)
                return true
            }
            else -> {
                // Verificamos si el caracter es válido (letra o número)
                if (char.isLetterOrDigit()) {
                    rfidBuffer.append(char)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun signingProcess(id: String) { //Funcion que procesa el codigo
        runOnUiThread {
            // Verificamos que estemos en la pantalla de login
            if (navController.currentDestination?.route == "login") {
                //navegamos a la pantalla de confirmacion
                navController.navigate("confirmation") {
                    employeeViewModel.searchByRfid(id)
                    launchSingleTop = true
                }
            }
        }
    }
}