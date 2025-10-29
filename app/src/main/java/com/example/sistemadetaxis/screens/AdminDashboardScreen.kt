package com.example.sistemadetaxis.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AdminDashboardScreen(onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("Registered Passengers")
            // TODO: Display registered passengers
            Text("Registered Taxi Drivers")
            // TODO: Display registered taxi drivers
        }
    }

}
