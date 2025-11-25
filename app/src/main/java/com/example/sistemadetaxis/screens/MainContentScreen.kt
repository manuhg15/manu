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
import com.example.sistemadetaxis.data.UserRole

@Composable
fun MainContentScreen(
    currentUserRole: UserRole?,
    loggedInUserId: String?,
    onLogout: () -> Unit,
    onViewProfile: () -> Unit // Added this
) {
    when (currentUserRole) {
        UserRole.PASSENGER -> {
            if (loggedInUserId != null) {
                PassengerScreen(passengerId = loggedInUserId, onViewProfile = onViewProfile, onLogout = onLogout)
            }
        }
        UserRole.DRIVER -> {
            if (loggedInUserId != null) {
                DriverScreen(driverId = loggedInUserId, onViewProfile = onViewProfile, onLogout = onLogout)
            }
        }
        // ✅ CORRECCIÓN: Agregar el caso ADMIN. Asumimos que no debería estar aquí, pero lo manejamos.
        UserRole.ADMIN -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: Administrador accedió al contenido principal de usuario.", textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
            }
        }
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Por favor, inicia sesión para acceder al servicio.", textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
            }
        }
    }
}