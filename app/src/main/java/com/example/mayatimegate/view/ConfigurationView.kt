package com.example.mayatimegate.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


/**
 * Vista de configuracion de la aplicacion
 */
@Composable
fun ConfigurationView(navController: NavHostController) {

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
        )
    }
}

@Composable
fun ConfigurationCompose(modifier: Modifier, onBackClick: () -> Unit){
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        BackRow(onBackClick = onBackClick)
    }
}
