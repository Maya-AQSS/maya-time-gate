package com.example.mayatimegate.views


import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mayatimegate.R

@Composable
fun ManualIdentificationView(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        ManualIdentificationCompose(
            modifier = Modifier.padding(innerPadding),
            onBackClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            },
            onManualClick = {
                navController.navigate("confirmation"){
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Composable
fun ManualIdentificationCompose(modifier: Modifier, onBackClick: () -> Unit, onManualClick: () -> Unit) {
    var dni by remember{mutableStateOf("")}
    var dniIsError by remember{mutableStateOf(false)}
    var pass by remember{mutableStateOf("")}
    var passIsError by remember{mutableStateOf(false)}
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
                    if (dniIsError && value.length >= 8){
                        dniIsError = false
                    }
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text("Contraseña", style = MaterialTheme.typography.titleLarge)
            PassTextField(
                isError = passIsError,
                onValueReady = { value ->
                    pass = value
                }
            )
            Spacer(modifier = Modifier.height(50.dp))
            RegisterButton(
                onClick = {
                    if(dni.length >= 8 && pass.isNotEmpty()){
                         onManualClick()
                    }else{
                        dniIsError = true
                        passIsError = true
                    }
                }

            )
        }
    }
}

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
            painterResource(R.drawable.ic_arrow_back),
            contentDescription = "Volver",
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text("Volver", style = MaterialTheme.typography.bodyLarge)
    }
}
@Composable
fun TitleScreen(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            "Identificación Manual",
            color = Color(0xFF0D6DFB),
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            "Introduce tus credenciales para fichar",
            style = MaterialTheme.typography.displaySmall
        )
    }

}

@Composable
fun IdentityTextField(isError: Boolean, onValueReady: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            onValueReady(it)
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = {
            Text(text = "Ej: 12345678", color = Color.Gray, style = MaterialTheme.typography.titleMedium)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_badge),
                contentDescription = "Icono identidad",
                modifier = Modifier.size(35.dp),
                tint = Color.Black
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray,
            errorTextColor = Color.Black,
            focusedBorderColor = Color(0xFF0D6DFB),
            unfocusedBorderColor = Color.LightGray,
            errorBorderColor = Color.Red
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = {
            if (textState.isNotEmpty()) {
                IconButton(onClick = { textState = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Borrar texto",
                        tint = Color.Black
                    )
                }
            }
            else if (isError) {
                Icon(Icons.Default.Info, "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    text = "El DNI debe tener al menos 8 números",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },

    )
}

@Composable
fun PassTextField(isError: Boolean, onValueReady: (String) ->Unit) {
    var textState by remember { mutableStateOf("") }

    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            onValueReady(it)
        },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = {
            Text(text = "Tu contraseña de Odoo", color = Color.Gray, style = MaterialTheme.typography.titleMedium)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = "Icono identidad",
                modifier = Modifier.size(35.dp),
                tint = Color.Black
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray,
            errorTextColor = Color.Black,
            focusedBorderColor = Color(0xFF0D6DFB),
            unfocusedBorderColor = Color.LightGray,
            errorBorderColor = Color.Red
        ),
        trailingIcon = {
            if (textState.isNotEmpty()) {
                IconButton(onClick = { textState = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Borrar texto",
                        tint = Color.Black
                    )
                }
            }else if (isError) {
                Icon(Icons.Default.Info, "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    text = "Introduce tu contraseña",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
    )
}

@Composable
fun RegisterButton(onClick: () -> Unit){
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth().
            padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF34495E),
        ),
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painterResource(R.drawable.ic_register),
                contentDescription = "Imagen teclado",
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                "Registrar Fichaje",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFFDFDFD)
            )
        }
    }
}

