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
import com.example.mayatimegate.views.LoginView
import com.example.mayatimegate.views.ManualIdentificationView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayaTimeGateTheme {
                val navController = rememberNavController()

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

                    composable("confirmation"){
                        ConfirmationView(
                            onTimeOver = {
                                navController.popBackStack()
                            },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}


