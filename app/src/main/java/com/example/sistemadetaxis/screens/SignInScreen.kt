package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.DataSource

@Composable
fun SignInScreen(
    role: String,
    onSignInSuccess: (String) -> Unit
) {
    var credential by remember { mutableStateOf("") } // Can be email or phone
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val roleName = if (role == "passenger") "Pasajero" else "Taxista"
    val credentialLabel = if (role == "passenger") "Correo Electrónico" else "Número de Teléfono"
    val keyboardType = if (role == "passenger") KeyboardType.Email else KeyboardType.Phone

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar Sesión como $roleName", fontSize = 28.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = credential,
            onValueChange = { credential = it },
            label = { Text(credentialLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )

        error?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                val user = if (role == "passenger") {
                    DataSource.findPassengerByEmail(credential, password)
                } else {
                    DataSource.findDriverByPhone(credential, password)
                }

                if (user != null) {
                    val userId = if (user is com.example.sistemadetaxis.data.Passenger) user.id else (user as com.example.sistemadetaxis.data.TaxiDriver).id
                    onSignInSuccess(userId)
                } else {
                    error = "Credenciales incorrectas. Inténtalo de nuevo."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}