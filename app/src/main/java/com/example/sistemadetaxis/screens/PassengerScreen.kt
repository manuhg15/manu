package com.example.sistemadetaxis.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.DataSource
import com.example.sistemadetaxis.TaxiDriver

@Composable
fun PassengerScreen() {
    val availableDrivers = DataSource.drivers.filter { it.isAvailable }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7E6))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Taxis disponibles",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        if (availableDrivers.isEmpty()) {
            Text("No hay taxistas disponibles en este momento.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(availableDrivers) { driver ->
                    DriverInfoCard(driver)
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
            Text("Vehículo: ${driver.vehicleType} (#${driver.taxiNumber})")
            Text("Placa: ${driver.licensePlate}")
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${driver.phoneNumber}"))
                    context.startActivity(intent)
                }) {
                    Text("Llamar")
                }
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
