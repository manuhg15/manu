package com.example.sistemadetaxis.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importar las clases de datos
import com.example.sistemadetaxis.data.FirebaseService
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.TaxiDriver
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerScreen(
    passengerId: String,
    onViewProfile: () -> Unit,
    onLogout: () -> Unit
) {
    // ✅ ESTADOS PARA DATOS Y CARGA
    var passenger by remember { mutableStateOf<Passenger?>(null) }
    var availableDrivers by remember { mutableStateOf<List<TaxiDriver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    // ✅ CARGA ASÍNCRONA DE DATOS DE FIREBASE
    LaunchedEffect(passengerId) {
        scope.launch {
            isLoading = true

            // 1. Obtener detalles del pasajero
            passenger = FirebaseService.getPassengerDetails(passengerId)

            // 2. Obtener todos los conductores y filtrar por disponibilidad y confirmación
            val allDrivers = FirebaseService.getAllDrivers()
            availableDrivers = allDrivers
                .filter { it.isAvailable && it.isConfirmed } // Solo taxistas confirmados y disponibles
                .sortedBy { it.name } // Opcional: ordenar por nombre

            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // Mostrar solo el nombre (o "Pasajero" si está cargando)
                title = { Text("Hola, ${passenger?.name?.split(" ")?.get(0) ?: "Cargando..."}") },
                actions = {
                    UserMenu(onViewProfile = onViewProfile, onLogout = { FirebaseService.signOut(); onLogout() }) // Usar signOut real
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF7E6))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Taxis disponibles",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isLoading) {
                // Muestra un indicador mientras Firebase está cargando
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            } else if (availableDrivers.isEmpty()) {
                Text("No hay taxistas disponibles en este momento.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(availableDrivers, key = { it.id }) { driver ->
                        DriverInfoCard(driver)
                    }
                }
            }
        }
    }
}

@Composable
fun DriverInfoCard(driver: TaxiDriver) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(driver.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Vehículo: ${driver.vehicleType}")
            Text("Placa: ${driver.licensePlate}")
            Text("Número de Taxi: ${driver.taxiNumber}")

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Botón LLAMAR (Lanza el marcador de teléfono)
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${driver.phoneNumber}"))
                    context.startActivity(intent)
                }) {
                    Text("Llamar")
                }
                // Botón MENSAJE (Lanza la aplicación de SMS)
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${driver.phoneNumber}"))
                    context.startActivity(intent)
                }) {
                    Text("Mensaje")
                }
            }
        }
    }
}