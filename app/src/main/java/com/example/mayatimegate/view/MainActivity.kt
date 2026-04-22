package com.example.mayatimegate.view

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mayatimegate.ui.theme.MayaTimeGateTheme
import com.example.mayatimegate.viewmodel.EmployeeViewModel
import com.example.mayatimegate.data.SettingsManager
import com.example.mayatimegate.utils.SoundManager
import com.example.mayatimegate.viewmodel.EmployeeViewModelFactory
import com.example.mayatimegate.viewmodel.SettingsViewModel
import com.example.mayatimegate.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    private val rfidBuffer = StringBuilder()
    private var RFIDCode = mutableStateOf("")
    private lateinit var navController: NavHostController

    // Usamos by viewModels con un delegado que inicializa la Factory.
    private val employeeViewModel: EmployeeViewModel by viewModels {
        EmployeeViewModelFactory(SettingsManager(this))
    }
    //val context = LocalContext.current

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Inicializamos el SettingsManager
        val settingsManager = SettingsManager(this)
        val soundManager = SoundManager(this)

        setContent {
            //Obtenemos el SettingsViewModel usando su Factory específica
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(application, settingsManager)
            )

            MayaTimeGateTheme {
                navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {

                    //Navegacion a las demas vistas
                    composable("login") {
                        LoginView(
                            onManualClick = {
                                navController.navigate("manual_id") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onConfigClick = {
                                navController.navigate("configuration") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },

                        )
                    }

                    composable("manual_id") {
                        ManualIdentificationView(
                            onTimeOver = { navController.popBackStack() },
                            navController = navController,
                            viewModel = employeeViewModel
                        )
                    }

                    composable("confirmation") {
                        ConfirmationView(
                            onTimeOver = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            viewModel = employeeViewModel,
                            navController = navController,
                            soundManager = soundManager
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
                            viewModel = employeeViewModel,
                            soundManager = soundManager
                        )
                    }

                    composable("connection_error") {
                        ConnectionErrorView(
                            onTimeOver = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            navController = navController,
                            soundManager = soundManager
                        )
                    }

                    composable("configuration") {
                        ConfigurationView(
                            onTimeOver = { navController.popBackStack() },
                            navController = navController,
                            viewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val char = event.unicodeChar.toChar()

        when (keyCode) {
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
                if (char.isLetterOrDigit()) {
                    rfidBuffer.append(char)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun signingProcess(id: String) {
        runOnUiThread {
            // Solo procesamos si estamos en la pantalla de login para evitar saltos inesperados
            if (navController.currentDestination?.route == "login") {
                employeeViewModel.searchByRfid(id)
                navController.navigate("confirmation") {
                    launchSingleTop = true
                }
            }
        }
    }
}