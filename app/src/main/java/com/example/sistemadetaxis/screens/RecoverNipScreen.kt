package com.example.sistemadetaxis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.sistemadetaxis.data.FirebaseService
import kotlinx.coroutines.launch // Necesario para CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverNipScreen(
    role: String,
    onBackClick: () -> Unit // Función para regresar a la pantalla anterior (SignIn)
) {
    var phone by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // ✅ ESTADOS PARA ASINCRONÍA
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val roleName = if (role == "passenger") "Pasajero" else "Taxista"

    fun onTextChange(value: String): String {
        return value.replace(Regex("[\r\n]"), "")
    }

    // ✅ FUNCIÓN DE RECUPERACIÓN ASÍNCRONA
    fun attemptRecovery() {
        statusMessage = null
        if (phone.length != 10 || isLoading) {
            statusMessage = "Por favor, ingresa un número de teléfono válido de 10 dígitos."
            return
        }

        isLoading = true // Iniciar carga

        scope.launch {
            // 1. Llamar al servicio de restablecimiento de contraseña de Firebase Auth.
            // Nota: El servicio restablece el NIP/contraseña asociado al "email" (phone@taxiapp.com).
            val success = FirebaseService.resetNipByPhone(phone)

            isLoading = false // Finalizar carga

            if (success) {
                // Éxito: El NIP ha sido cambiado a 1234 en el backend.
                statusMessage = "✅ ¡Restablecimiento exitoso! Tu NIP temporal (1234) ha sido enviado. Regresando a Iniciar Sesión..."

                // 2. Esperar brevemente (opcional, para que el usuario vea el mensaje)
                kotlinx.coroutines.delay(1000)

                // 3. Cierra la pantalla para que el usuario pueda intentar hacer Login
                onBackClick()

            } else {
                // Fallo: Si el teléfono no está asociado a ninguna cuenta de Firebase Auth.
                statusMessage = "❌ Error: Teléfono no encontrado para el rol de $roleName o error de red."
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar NIP ($roleName)") },
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
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Ingresa el teléfono registrado para restablecer tu NIP. Recibirás el NIP temporal (1234) por mensaje.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    val text = onTextChange(it)
                    if (text.length <= 10 && text.all { char -> char.isDigit() }) {
                        phone = text
                    }
                },
                label = { Text("Número de Teléfono (10 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading // Deshabilitar durante la carga
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { attemptRecovery() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && phone.length == 10 // Deshabilitar durante la carga
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Recuperar NIP")
                }
            }

            // Mostrar mensaje de estado
            statusMessage?.let {
                Text(
                    it,
                    // Usamos Color.Green para éxito, Color.Red para error
                    color = if (it.startsWith("✅")) Color.Green.copy(alpha = 0.8f) else Color.Red,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}