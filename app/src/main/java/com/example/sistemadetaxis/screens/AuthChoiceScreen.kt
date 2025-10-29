package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthChoiceScreen(
    role: String,
    onAuthSuccess: () -> Unit
) {
    val roleName = if (role == "passenger") "Pasajero" else "Taxista"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Inicia sesión o regístrate",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp),
            lineHeight = 40.sp
        )
        // In a real app, these would navigate to different screens with forms.
        // For now, both will act as a successful login/registration.
        Button(
            onClick = { onAuthSuccess() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión como $roleName")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAuthSuccess() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse como $roleName")
        }
    }
}