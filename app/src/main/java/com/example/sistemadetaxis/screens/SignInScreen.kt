package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.DataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    role: String,
    onSignInSuccess: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var credential by remember { mutableStateOf("") } // Can be email or phone
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val roleName = if (role == "passenger") "Pasajero" else "Taxista"
    val credentialLabel = if (role == "passenger") "Correo Electrónico" else "Número de Teléfono"
    val keyboardType = if (role == "passenger") KeyboardType.Email else KeyboardType.Phone
    val focusManager = LocalFocusManager.current

    fun onTextChange(value: String): String {
        return value.replace(Regex("[\r\n]"), "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") }, // Simplified title
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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Added the title back to the main content area
            Text("Acceder como $roleName", fontSize = 28.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 24.dp))

            OutlinedTextField(
                value = credential,
                onValueChange = { credential = onTextChange(it) },
                label = { Text(credentialLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = onTextChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
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
                })
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
}