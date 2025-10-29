package com.example.sistemadetaxis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.DataSource
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.TaxiDriver
import com.example.sistemadetaxis.data.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    userRole: UserRole,
    onBackClick: () -> Unit,
    onEditProfile: (String, UserRole) -> Unit
) {
    val user: Any? = when (userRole) {
        UserRole.PASSENGER -> DataSource.findPassengerById(userId)
        UserRole.DRIVER -> DataSource.findDriverById(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) { Text("Usuario no encontrado") }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (user) {
                is Passenger -> {
                    Text("Nombre: ${user.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Correo: ${user.email}", fontSize = 16.sp)
                    Text("Zona Principal: ${user.mainZone}", fontSize = 16.sp)
                }
                is TaxiDriver -> {
                    Text("Nombre: ${user.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Teléfono: ${user.phoneNumber}", fontSize = 16.sp)
                    Text("Vehículo: ${user.vehicleType} (#${user.taxiNumber})", fontSize = 16.sp)
                    Text("Placa: ${user.licensePlate}", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { onEditProfile(userId, userRole) }) {
                Text("Editar Perfil")
            }
        }
    }
}