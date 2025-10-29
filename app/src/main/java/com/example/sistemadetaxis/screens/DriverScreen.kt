package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.DataSource

@Composable
fun DriverScreen(driverId: String) {
    val driver = DataSource.findDriverById(driverId) ?: return
    // Observe changes from the data source
    val isAvailable = DataSource.drivers.first { it.id == driverId }.isAvailable

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Panel de Conductor", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hola, ${driver.name}", fontSize = 22.sp)
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