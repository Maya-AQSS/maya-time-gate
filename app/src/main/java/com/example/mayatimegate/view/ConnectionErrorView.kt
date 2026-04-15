package com.example.mayatimegate.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
@Composable
fun ConnectionErrorView(
    onTimeOver: () -> Unit,
    navController: NavHostController,
) {

    // Temporizador de visualizacion
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeOver()
    }

    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ConnectionErrorCompose(
            modifier = Modifier.padding(innerPadding),
            onClick = {
                // Navegación manual: limpia la pila para evitar bucles en la pantalla de error
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
fun ConnectionErrorCompose(modifier: Modifier, onClick: () -> Unit){

}
