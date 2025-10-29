package com.example.sistemadetaxis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.DataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    onAddPassenger: () -> Unit,
    onAddDriver: () -> Unit
) {
    val passengers = DataSource.passengers
    val drivers = DataSource.drivers

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    Button(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onAddPassenger) { Text("Añadir Pasajero") }
                    Button(onClick = onAddDriver) { Text("Añadir Taxista") }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Pasajeros Registrados", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(passengers) { passenger ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${passenger.name}", fontWeight = FontWeight.Bold)
                        Text("Email: ${passenger.email}")
                        Text("Zona: ${passenger.mainZone}")
                        Text("Contraseña: ${passenger.password}")
                        Button(onClick = { DataSource.removePassenger(passenger) }) {
                            Text("Eliminar")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Taxistas Registrados", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(drivers) { driver ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${driver.name}", fontWeight = FontWeight.Bold)
                        Text("Teléfono: ${driver.phoneNumber}")
                        Text("Vehículo: ${driver.vehicleType}")
                        Text("Taxi No: ${driver.taxiNumber}")
                        Text("Placa: ${driver.licensePlate}")
                        Text("Contraseña: ${driver.password}")
                        Button(onClick = { DataSource.removeDriver(driver) }) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}
