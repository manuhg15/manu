package com.example.sistemadetaxis.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sistemadetaxis.UserRole

@Composable
fun MainContentScreen(currentUserRole: UserRole?, loggedInDriverId: String) {
    when (currentUserRole) {
        UserRole.PASSENGER -> PassengerScreen()
        UserRole.DRIVER -> DriverScreen(driverId = loggedInDriverId)
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Por favor, inicia sesión en la pestaña 'Acceder'", textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
            }
        }
    }
}