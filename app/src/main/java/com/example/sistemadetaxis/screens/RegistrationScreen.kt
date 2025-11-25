package com.example.sistemadetaxis.screens

// Importaciones de Compose Foundation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

// Importaciones de Compose Material3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*

// Importaciones de Compose Runtime y UI
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Importaciones de Firebase Service y Coroutines
import com.example.sistemadetaxis.data.FirebaseService
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    role: String,
    onRegisterSuccess: () -> Unit, // Navegará a SignIn
    onBackClick: () -> Unit
) {
    // Variables de Estado
    var firstName by remember { mutableStateOf("") }
    var paternalLastName by remember { mutableStateOf("") }
    var maternalLastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isPhoneError by remember { mutableStateOf(false) }
    var nip by remember { mutableStateOf("") }
    var isNipError by remember { mutableStateOf(false) }
    var mainZone by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var vehicleType by remember { mutableStateOf("") }
    var taxiNumber by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }

    // ESTADOS PARA ASINCRONÍA Y ERRORES
    var registrationError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Scope para lanzar corrutinas


    val zones = listOf("Huamantla Centro", "San Sebastián", "Santa Anita", "P. la Cruz", "El Carmen", "Zaragoza", "San Francisco Yancuitlalpan", "San José Xicohténcatl")
    val roleName = if (role == "passenger") "Pasajero" else "Taxista"
    val focusManager = LocalFocusManager.current

    // VALIDACIONES DEL FORMULARIO
    fun onTextChange(value: String): String { return value.replace(Regex("[\\r\\n]"), "") }
    fun validatePhone(phone: String): Boolean { return phone.length == 10 && phone.all { it.isDigit() } }

    // ✅ VALIDACIÓN DE 6 DÍGITOS
    fun validateNip(nip: String): Boolean { return nip.length == 6 && nip.all { it.isDigit() } }

    val isPassengerFormValid =
        firstName.isNotBlank() && paternalLastName.isNotBlank() && maternalLastName.isNotBlank() &&
                validatePhone(phone) && mainZone.isNotBlank() && validateNip(nip)

    val isDriverFormValid =
        firstName.isNotBlank() && paternalLastName.isNotBlank() && maternalLastName.isNotBlank() &&
                validatePhone(phone) && vehicleType.isNotBlank() && taxiNumber.isNotBlank() &&
                licensePlate.isNotBlank() && validateNip(nip)

    val isCurrentFormValid = if (role == "passenger") isPassengerFormValid else isDriverFormValid

    // FUNCIÓN DE REGISTRO ASÍNCRONA
    fun attemptRegistration() {
        registrationError = null

        if (!isCurrentFormValid || isLoading) return

        isLoading = true
        val fullName = "$firstName $paternalLastName $maternalLastName"

        scope.launch { // LLAMADA ASÍNCRONA A FIREBASE
            val userId = if (role == "passenger") {
                FirebaseService.registerPassenger(fullName, phone, mainZone, nip)
            } else {
                FirebaseService.registerDriver(fullName, phone, vehicleType, taxiNumber, licensePlate, nip)
            }

            isLoading = false // Finalizar carga

            if (userId != null) {
                // Éxito: El usuario se creó en Auth y Firestore
                onRegisterSuccess()
            } else {
                // Fallo: Error de unicidad de Firebase Auth (teléfono duplicado) o error de red.
                registrationError = "El número de teléfono ya está registrado como ${roleName} o hay un error de conexión."
            }
        }
    }


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
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // 1. CAMPOS DE DATOS PERSONALES
            OutlinedTextField(value = firstName, onValueChange = { firstName = onTextChange(it) }, label = { Text("Nombres*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = paternalLastName, onValueChange = { paternalLastName = onTextChange(it) }, label = { Text("Apellido Paterno*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = maternalLastName, onValueChange = { maternalLastName = onTextChange(it) }, label = { Text("Apellido Materno*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            Spacer(Modifier.height(16.dp))

            // 2. NÚMERO DE TELÉFONO (Restricción 10 dígitos)
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    val text = onTextChange(it)
                    if (text.length <= 10 && text.all { char -> char.isDigit() }) {
                        phone = text
                        isPhoneError = text.isNotBlank() && !validatePhone(text)
                    }
                },
                label = { Text("Número de Teléfono* (10 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth(),
                isError = isPhoneError || registrationError != null,
                singleLine = true
            )
            if (isPhoneError) {
                Text("El teléfono debe ser numérico y tener 10 dígitos.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // 3. CAMPOS ESPECÍFICOS POR ROL
            if (role == "passenger") {
                // ZONA DE RESIDENCIA (OBLIGATORIO)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = mainZone, onValueChange = { }, label = { Text("Zona de Residencia*") },
                        readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(), isError = mainZone.isBlank()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        zones.forEach { zone ->
                            DropdownMenuItem(text = { Text(zone) }, onClick = { mainZone = zone; expanded = false; focusManager.moveFocus(FocusDirection.Down) })
                        }
                    }
                }
            } else {
                // CAMPOS DE CONDUCTOR (TODOS OBLIGATORIOS)
                OutlinedTextField(value = vehicleType, onValueChange = { vehicleType = onTextChange(it) }, label = { Text("Tipo de Vehículo*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                Spacer(Modifier.height(16.dp))

                // NÚMERO DE TAXI (Solo numérico)
                OutlinedTextField(
                    value = taxiNumber,
                    onValueChange = {
                        val text = onTextChange(it)
                        if (text.all { char -> char.isDigit() }) {
                            taxiNumber = text
                        }
                    },
                    label = { Text("Número de Taxi*") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = onTextChange(it) }, label = { Text("Placa*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            }

            Spacer(Modifier.height(16.dp))

            // 4. CAMPO DE NIP (OBLIGATORIO PARA AMBOS)
            OutlinedTextField(
                value = nip,
                onValueChange = {
                    val text = onTextChange(it)
                    // ✅ CORRECCIÓN DE NIP: 6 dígitos
                    if (text.length <= 6 && text.all { char -> char.isDigit() }) { nip = text }
                    isNipError = text.length != 6 && text.isNotBlank() // Valida longitud de 6
                },
                label = { Text("NIP de Acceso* (6 dígitos)") }, // Actualiza etiqueta
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(), singleLine = true, isError = isNipError
            )
            if (isNipError && nip.isNotBlank()) {
                Text("El NIP debe ser de 6 dígitos.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))

            // Mostrar Error de Registro (Teléfono Duplicado o Firebase)
            registrationError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(Modifier.height(8.dp))

            // ✅ BOTÓN REGISTRARSE (ASÍNCRONO)
            Button(
                onClick = { attemptRegistration() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && isCurrentFormValid // Deshabilitar durante la carga
            ) {
                if (isLoading) {
                    // Mostrar indicador de progreso durante la llamada a Firebase
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Registrarse")
                }
            }
        }
    }
}