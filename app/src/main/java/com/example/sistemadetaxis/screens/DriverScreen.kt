package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.DataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverScreen(
    driverId: String,
    onViewProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val driver = DataSource.findDriverById(driverId) ?: return
    val isAvailable = DataSource.drivers.first { it.id == driverId }.isAvailable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola, ${driver.name.split(" ").get(0)}") },
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

            Spacer(modifier = Modifier.height(48.dp))

            Text("Mi Panel de Conductor", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

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
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { DataSource.toggleDriverAvailability(driver.id) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isAvailable) "Aparecerás en la lista de los pasajeros." else "No serás visible para los pasajeros.",
                textAlign = TextAlign.Center
            )
        }
    }
}