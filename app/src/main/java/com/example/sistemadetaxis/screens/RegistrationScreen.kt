package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sistemadetaxis.data.DataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    role: String,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mainZone by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var taxiNumber by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }

    val roleName = if (role == "passenger") "Pasajero" else "Taxista"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de $roleName") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(innerPadding)
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            if (role == "passenger") {
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = mainZone, onValueChange = { mainZone = it }, label = { Text("Zona de Residencia") }, modifier = Modifier.fillMaxWidth())
            } else {
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Número de Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = vehicleType, onValueChange = { vehicleType = it }, label = { Text("Tipo de Vehículo") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = taxiNumber, onValueChange = { taxiNumber = it }, label = { Text("Número de Taxi") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = it }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (role == "passenger") {
                        DataSource.registerPassenger(name, email, mainZone, password)
                    } else {
                        DataSource.registerDriver(name, phone, vehicleType, taxiNumber, licensePlate, password)
                    }
                    onRegisterSuccess()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}