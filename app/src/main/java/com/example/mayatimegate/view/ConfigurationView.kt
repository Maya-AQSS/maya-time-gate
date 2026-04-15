package com.example.mayatimegate.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        )
    }
}

@Composable
fun ConfigurationCompose(modifier: Modifier){

}
