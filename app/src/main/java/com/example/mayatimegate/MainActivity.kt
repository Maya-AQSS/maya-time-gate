package com.example.mayatimegate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mayatimegate.ui.theme.MayaTimeGateTheme
import com.example.mayatimegate.views.ConfirmationView
import com.example.mayatimegate.views.ErrorView
import com.example.mayatimegate.views.LoginView
import com.example.mayatimegate.views.ManualIdentificationView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el diseño de borde a borde (detrás de las barras de sistema)

        setContent {
            MayaTimeGateTheme {
                // Controlador central para gestionar el historial y cambio de pantallas
                val navController = rememberNavController()

                // Definición del grafo de navegación de la aplicación
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {

                    composable("login") {
                        LoginView(navController)
                    }

                    composable("manual_id") {
                        ManualIdentificationView(navController)
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
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}