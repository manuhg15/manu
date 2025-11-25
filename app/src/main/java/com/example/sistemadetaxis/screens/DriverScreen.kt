package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.FirebaseService
import com.example.sistemadetaxis.data.TaxiDriver
import kotlinx.coroutines.launch // Necesario para CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverScreen(
    driverId: String,
    onViewProfile: () -> Unit,
    onLogout: () -> Unit
) {
    // ✅ ESTADOS DE DATOS Y CARGA
    var driver by remember { mutableStateOf<TaxiDriver?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Obtener estado actual del conductor de forma segura
    val isAvailable = driver?.isAvailable ?: false

    // ✅ CARGA ASÍNCRONA DE DATOS AL INICIO
    LaunchedEffect(driverId) {
        scope.launch {
            isLoading = true

            // Llamada asíncrona a FirebaseService
            driver = FirebaseService.getDriverDetails(driverId)

            // CRÍTICO: Verificar si el conductor está aprobado
            if (driver == null || !driver!!.isConfirmed) {
                // Si no se encuentra o no está confirmado, cerrar sesión y notificar
                statusMessage = "Tu cuenta está inactiva o pendiente de aprobación. Cierre de sesión por seguridad."
                FirebaseService.signOut()
            }

            isLoading = false
        }
    }

    // ✅ FUNCIÓN ASÍNCRONA para cambiar la disponibilidad
    fun toggleAvailability(checked: Boolean) {
        if (driver == null || isLoading || !driver!!.isConfirmed) return // Bloquear si no está confirmado

        // Optimización de UI: Cambiamos el estado local inmediatamente
        driver = driver!!.copy(isAvailable = checked)

        scope.launch {
            // Llamada asíncrona a FirebaseService para actualizar Firestore
            val success = FirebaseService.toggleDriverAvailability(driverId, checked)

            if (!success) {
                // Si falla la red, revertimos el estado local y mostramos error
                driver = driver!!.copy(isAvailable = !checked)
                statusMessage = "Error de red al actualizar estado."
            } else {
                statusMessage = null
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola, ${driver?.name?.split(" ")?.get(0) ?: "Conductor"}") },
                actions = {
                    UserMenu(onViewProfile = onViewProfile, onLogout = onLogout)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (isLoading) {
                // Muestra el indicador mientras se cargan los datos del perfil
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                return@Column
            }

            // Si el conductor no existe o no está aprobado
            if (driver == null || !driver!!.isConfirmed) {
                Text(
                    statusMessage ?: "Error crítico en la cuenta. Intente iniciar sesión nuevamente.",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onLogout) {
                    Text("Cerrar Sesión")
                }
                return@Column
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text("Panel de Conductor", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            // CONTROL DE DISPONIBILIDAD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mi Estado:", fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text(
                    text = if (isAvailable) "Disponible" else "No Disponible",
                    color = if (isAvailable) Color(0xFF008000) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(16.dp))

                // Switch llama a la función asíncrona de toggle
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { toggleAvailability(it) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isAvailable) "Aparecerás en la lista de los pasajeros y podrás recibir viajes." else "No serás visible para los pasajeros ni recibirás solicitudes.",
                textAlign = TextAlign.Center
            )

            // Mostrar mensaje de error/éxito si existe
            statusMessage?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}