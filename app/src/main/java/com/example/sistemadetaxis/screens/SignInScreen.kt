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
// ✅ IMPORTACIÓN DEL SERVICIO FIREBASE
import com.example.sistemadetaxis.data.FirebaseService
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.TaxiDriver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch // Necesario para CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    role: String,
    onSignInSuccess: (String) -> Unit,
    onBackClick: () -> Unit,
    onRecoverNipClick: (String) -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isPhoneError by remember { mutableStateOf(false) }
    var isNipError by remember { mutableStateOf(false) }

    // ✅ ESTADOS PARA ASINCRONÍA
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Scope para lanzar corrutinas

    val roleName = if (role == "passenger") "Pasajero" else "Taxista"
    val focusManager = LocalFocusManager.current

    // VALIDACIONES DEL FORMULARIO
    fun onTextChange(value: String): String { return value.replace(Regex("[\r\n]"), "") }
    fun validatePhone(phone: String): Boolean { return phone.length == 10 && phone.all { it.isDigit() } }

    // ✅ CORRECCIÓN: NIP DEBE SER DE 6 DÍGITOS
    fun validateNip(nip: String): Boolean { return nip.length == 6 && nip.all { it.isDigit() } }


    // ✅ Lógica principal de inicio de sesión ASÍNCRONA
    fun attemptSignIn() {
        error = null
        isPhoneError = !validatePhone(phone)
        // ✅ USAR VALIDACIÓN DE 6 DÍGITOS
        isNipError = !validateNip(nip)

        if (isPhoneError || isNipError || isLoading) {
            // ✅ CORREGIR MENSAJE DE ERROR
            error = "Por favor, verifica el Teléfono (10 dígitos) y el NIP (6 dígitos)."
            return
        }

        isLoading = true // Iniciar carga

        scope.launch { // ✅ LLAMADA ASÍNCRONA EN COROUTINE

            // 1. Intentar iniciar sesión en Firebase Auth
            val userId = FirebaseService.signIn(phone, nip)

            if (userId != null) {
                // 2. Auth exitoso. Ahora verifica el rol y la aprobación en Firestore.
                val user: Any? = if (role == "passenger") {
                    FirebaseService.getPassengerDetails(userId)
                } else {
                    FirebaseService.getDriverDetails(userId)
                }

                if (user != null) {
                    if (role == "driver" && user is TaxiDriver && !user.isConfirmed) {
                        // 3. Login de conductor fallido por aprobación pendiente
                        error = "Tu solicitud como Conductor está pendiente de aprobación por el administrador."
                        FirebaseService.signOut() // Cierra la sesión activa de Auth
                    } else {
                        // 4. Login exitoso y aprobado
                        onSignInSuccess(userId)
                    }
                } else {
                    // Si Auth fue exitoso pero el documento no existe (error de datos)
                    error = "Error de datos: Usuario no encontrado en Firestore."
                    FirebaseService.signOut()
                }

            } else {
                // Login fallido en Auth.
                error = "Credenciales incorrectas. Teléfono o NIP inválido."
            }

            isLoading = false // Finalizar carga
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
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
            // Se eliminó el título duplicado

            // 1. TELÉFONO (GUARDADO)
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    val text = onTextChange(it)
                    if (text.length <= 10 && text.all { char -> char.isDigit() }) {
                        phone = text
                    }
                    isPhoneError = text.isNotBlank() && text.length != 10
                },
                label = { Text("Número de Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isPhoneError || error != null
            )
            if (isPhoneError && phone.isNotBlank()) {
                Text("El teléfono debe tener 10 dígitos.", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            // 2. NIP DE ACCESO (6 DÍGITOS)
            OutlinedTextField(
                value = nip,
                onValueChange = {
                    val text = onTextChange(it)
                    // ✅ CORRECCIÓN: Permitir MÁXIMO 6 dígitos
                    if (text.length <= 6 && text.all { char -> char.isDigit() }) {
                        nip = text
                    }
                    isNipError = text.isNotBlank() && text.length != 6 // Validar que sea 6
                },
                label = { Text("NIP de Acceso (6 dígitos)") }, // ✅ ACTUALIZAR ETIQUETA
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { attemptSignIn() }),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isNipError || error != null
            )
            if (isNipError && nip.isNotBlank()) {
                Text("El NIP debe ser de 6 dígitos.", color = MaterialTheme.colorScheme.error) // ✅ ACTUALIZAR MENSAJE
            }

            error?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp), textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(32.dp))

            // ✅ BOTÓN ASÍNCRONO
            Button(
                onClick = { attemptSignIn() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && validatePhone(phone) && validateNip(nip)
            ) {
                if (isLoading) {
                    // Mostrar indicador de progreso durante la llamada a Firebase
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Iniciar Sesión")
                }
            }

            // ENLACE DE RECUPERACIÓN DE NIP
            TextButton(
                onClick = { onRecoverNipClick(role) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Olvidé mi NIP", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}