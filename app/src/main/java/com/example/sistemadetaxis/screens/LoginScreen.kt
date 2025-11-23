package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults // Necesario para personalizar el color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definición de colores según tus requisitos
val GreenButtonColor = Color(0xFF4CAF50) // Verde para PASAJERO
val PurpleButtonColor = Color(0xFF673AB7) // Morado para CONDUCTOR

@Composable
fun AuthChoiceScreen(onRoleSelected: (String) -> Unit, onAdminClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(horizontal = 32.dp), // Reducimos el padding vertical
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra los elementos verticalmente
        ) {
            // Se elimina el texto "Selecciona tu rol" y la pregunta.

            Button(
                onClick = { onRoleSelected("passenger") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Hace el botón más grande
                colors = ButtonDefaults.buttonColors(containerColor = GreenButtonColor)
            ) {
                Text(
                    text = "PASAJERO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Espacio entre botones

            Button(
                onClick = { onRoleSelected("driver") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Hace el botón más grande
                colors = ButtonDefaults.buttonColors(containerColor = PurpleButtonColor)
            ) {
                Text(
                    text = "CONDUCTOR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Botón Admin
        Button(
            onClick = onAdminClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleButtonColor)
        ) {
            Text("Admin")
        }
    }
}
