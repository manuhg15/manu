package com.example.sistemadetaxis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.FirebaseService
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.TaxiDriver
import com.example.sistemadetaxis.data.UserRole
import kotlinx.coroutines.launch // Necesario para CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    userRole: UserRole,
    onBackClick: () -> Unit,
    onEditProfile: (String, UserRole) -> Unit
) {
    // ✅ ESTADOS PARA DATOS Y CARGA
    var user by remember { mutableStateOf<Any?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // ✅ CARGA ASÍNCRONA DE DATOS AL INICIO DE LA PANTALLA
    LaunchedEffect(userId, userRole) {
        if (userRole == UserRole.ADMIN) {
            user = null // El admin no tiene un perfil de usuario simple
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            isLoading = true

            // Llama a la función asíncrona correcta (get*Details)
            user = when (userRole) {
                UserRole.PASSENGER -> FirebaseService.getPassengerDetails(userId)
                UserRole.DRIVER -> FirebaseService.getDriverDetails(userId)
                else -> null
            }

            isLoading = false
        }
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

        if (isLoading) {
            // Muestra un indicador mientras Firebase está cargando
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // Asignar el valor del estado a una variable local para el smart cast
        val currentUser = user

        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Usuario no encontrado o rol inválido.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // ✅ CORRECCIÓN: Usar 'currentUser' para que el smart cast funcione
            when (currentUser) {
                is Passenger -> {
                    Text("Rol: Pasajero", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nombre: ${currentUser.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Teléfono: ${currentUser.phone}", fontSize = 16.sp)
                    Text("Zona Principal: ${currentUser.mainZone}", fontSize = 16.sp)
                }
                is TaxiDriver -> {
                    Text("Rol: Conductor", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nombre: ${currentUser.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Teléfono: ${currentUser.phoneNumber}", fontSize = 16.sp)
                    Text("Vehículo: ${currentUser.vehicleType} (#${currentUser.taxiNumber})", fontSize = 16.sp)
                    Text("Placa: ${currentUser.licensePlate}", fontSize = 16.sp)

                    // Mostrar disponibilidad
                    Text(
                        text = if (currentUser.isAvailable) "Estado: DISPONIBLE" else "Estado: OCUPADO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Mostrar estado de aprobación
                    Text(
                        text = if (currentUser.isConfirmed) "Aprobación: CONFIRMADA" else "Aprobación: PENDIENTE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { onEditProfile(userId, userRole) }) {
                Text("Editar Perfil")
            }
        }
    }
}