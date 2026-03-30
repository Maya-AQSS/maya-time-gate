package com.example.mayatimegate.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mayatimegate.R
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController

@Composable
fun LoginView(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFFEEECEB)
    ) { innerPadding ->
        LoginCompose(
            modifier = Modifier.padding(innerPadding),
            onManualClick = {
                navController.navigate("manual_id") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Composable
fun LoginCompose(modifier: Modifier, onManualClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFDFDFD),
        ),
    ) {
        Column (
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painterResource(R.drawable.logo_ceedcv),
                contentDescription = "Imagen tarjeta",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            CurrentTime()
            Spacer(modifier = Modifier.height(15.dp))
            CurrentDate()
            Spacer(modifier = Modifier.height(40.dp))
            CardOption()
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 25.dp),
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.5f)
            )
            ManualOption(onClick = onManualClick)
        }
    }
}

@Composable
fun CurrentTime() {
    AndroidView(
        factory = { context ->
            android.widget.TextClock(context).apply {
                format12Hour = "HH:mm"
                format24Hour = "HH:mm"

                textSize = 50f
                setTextColor(android.graphics.Color.BLACK)

                gravity = android.view.Gravity.CENTER
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun CurrentDate(){
    val date = LocalDate.now()
    val formatt = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val currentDate = date.format(formatt)
    Text(
        currentDate,
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun CardOption(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

        border = BorderStroke(2.dp, Color(0xFFE1E5E8)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF6F7F8),
        ),
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.ic_badge),
                contentDescription = "Imagen tarjeta",
                modifier = Modifier.size(150.dp)
            )
            Text(
                "Acerca tu tarjeta al lector",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "El sistema registrará tu entrada automaticamente",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ManualOption(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 16.dp),
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
                painterResource(R.drawable.ic_keyboard),
                contentDescription = "Imagen teclado",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                "Identificación Manual (DNI/Clave)",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFFDFDFD)
            )
        }
    }
}

