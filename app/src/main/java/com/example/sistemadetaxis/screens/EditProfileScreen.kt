package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import com.example.sistemadetaxis.data.FirebaseService
import com.example.sistemadetaxis.data.TaxiDriver
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.UserRole
import kotlinx.coroutines.launch // Necesario para CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: String,
    userRole: UserRole,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    // ESTADOS DE DATOS Y CARGA
    var loadedUser by remember { mutableStateOf<Any?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var saveError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()


    // Variables del formulario
    var firstName by remember { mutableStateOf("") }
    var paternalLastName by remember { mutableStateOf("") }
    var maternalLastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isPhoneError by remember { mutableStateOf(false) }
    var mainZone by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var taxiNumber by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var isNipError by remember { mutableStateOf(false) }

    val zones = listOf("Centro", "San Sebastián", "Santa Anita", "La Trinidad", "El Carmen", "Guadalupe", "San Francisco Yancuitlalpan", "San José Xicohténcatl")
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun onTextChange(value: String): String {
        return value.replace(Regex("[\r\n]"), "")
    }

    // LÓGICA DE CARGA ASÍNCRONA
    LaunchedEffect(userId, userRole) {
        if (userRole == UserRole.ADMIN) {
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            isLoading = true

            // 1. Llamar a la función asíncrona correcta (get*Details)
            val fetchedUser = when (userRole) {
                UserRole.PASSENGER -> FirebaseService.getPassengerDetails(userId)
                UserRole.DRIVER -> FirebaseService.getDriverDetails(userId)
                else -> null
            }

            // 2. Pre-poblar el formulario
            if (fetchedUser != null) {
                loadedUser = fetchedUser // Guardar el usuario cargado

                val currentName = when (fetchedUser) {
                    is Passenger -> fetchedUser.name
                    is TaxiDriver -> fetchedUser.name
                    else -> ""
                }

                // ✅ CORRECCIÓN DE LA LÓGICA DE DIVISIÓN DE NOMBRES
                val nameParts = currentName.split(" ").filter { it.isNotBlank() }

                // Asumimos: Nombre(s) son todo menos los dos últimos apellidos.
                // Ejemplo: "Juan Manuel Hernández González" -> Nombre: "Juan Manuel", Paterno: "Hernández", Materno: "González"
                firstName = nameParts.subList(0, (nameParts.size - 2).coerceAtLeast(1)).joinToString(" ")
                paternalLastName = nameParts.getOrNull(nameParts.size - 2) ?: ""
                maternalLastName = nameParts.lastOrNull() ?: ""


                if (fetchedUser is Passenger) {
                    phone = fetchedUser.phone
                    mainZone = fetchedUser.mainZone
                    nip = fetchedUser.nip
                }
                if (fetchedUser is TaxiDriver) {
                    phone = fetchedUser.phoneNumber
                    vehicleType = fetchedUser.vehicleType
                    taxiNumber = fetchedUser.taxiNumber
                    licensePlate = fetchedUser.licensePlate
                    nip = fetchedUser.nip
                }
            }
            isLoading = false
        }
    }

    fun validatePhone(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }

    fun validateNip(nip: String): Boolean {
        // ✅ CORRECCIÓN: NIP debe ser de 6 dígitos
        return nip.length == 6 && nip.all { it.isDigit() }
    }

    // Verificación de que todos los campos obligatorios están llenos
    val isFormValid = when (userRole) {
        UserRole.PASSENGER -> {
            firstName.isNotBlank() && paternalLastName.isNotBlank() &&
                    maternalLastName.isNotBlank() && validatePhone(phone) &&
                    mainZone.isNotBlank() && !isPhoneError && validateNip(nip)
        }
        UserRole.DRIVER -> {
            firstName.isNotBlank() && paternalLastName.isNotBlank() &&
                    maternalLastName.isNotBlank() && validatePhone(phone) &&
                    vehicleType.isNotBlank() && taxiNumber.isNotBlank() &&
                    licensePlate.isNotBlank() && !isPhoneError && validateNip(nip)
        }
        UserRole.ADMIN -> false
    }

    // LÓGICA DE GUARDAR ASÍNCRONA
    fun attemptSave() {
        saveError = null
        if (!isFormValid || isLoading || loadedUser == null) return

        isLoading = true // Iniciar carga de guardado
        val fullName = "$firstName $paternalLastName $maternalLastName"

        scope.launch {
            var saveResult = false
            var isDuplicated = false

            if (userRole == UserRole.PASSENGER) {
                val passengerUser = loadedUser as Passenger
                // 1. Verifica si otro pasajero ya tiene este número (ASÍNCRONO)
                if (FirebaseService.isPhoneRegisteredAsPassenger(phone) && passengerUser.phone != phone) {
                    isDuplicated = true
                } else {
                    // 2. LLAMADA ASÍNCRONA DE ACTUALIZACIÓN
                    saveResult = FirebaseService.updatePassenger(userId, fullName, phone, mainZone, nip)
                }
            } else if (userRole == UserRole.DRIVER) {
                val driverUser = loadedUser as TaxiDriver
                // 1. Verifica si *otro conductor* ya tiene este número (ASÍNCRONO)
                if (FirebaseService.isPhoneRegisteredAsDriver(phone) && driverUser.phoneNumber != phone) {
                    isDuplicated = true
                } else {
                    // 2. LLAMADA ASÍNCRONA DE ACTUALIZACIÓN
                    saveResult = FirebaseService.updateDriver(userId, fullName, phone, vehicleType, taxiNumber, licensePlate, nip)
                }
            }

            isLoading = false // Finalizar carga

            if (isDuplicated) {
                saveError = "El número de teléfono ya está registrado por otro ${if (userRole == UserRole.PASSENGER) "pasajero" else "conductor"}."
            } else if (saveResult) {
                onSaveClick()
            } else {
                saveError = "Fallo al guardar los cambios. Verifique su conexión."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                return@Column
            }

            if (loadedUser == null) {
                Text("Error: Datos de usuario no cargados.", color = Color.Red)
                return@Column
            }

            Spacer(Modifier.height(24.dp))

            // CAMPOS DE NOMBRE
            OutlinedTextField(value = firstName, onValueChange = { firstName = onTextChange(it) }, label = { Text("Nombres*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = paternalLastName, onValueChange = { paternalLastName = onTextChange(it) }, label = { Text("Apellido Paterno*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = maternalLastName, onValueChange = { maternalLastName = onTextChange(it) }, label = { Text("Apellido Materno*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))

            // TELÉFONO (Restricción 10 dígitos)
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
                isError = isPhoneError,
                singleLine = true,
                enabled = !isLoading
            )
            if (isPhoneError) {
                Text("El teléfono debe ser numérico y tener 10 dígitos.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // CAMPOS ESPECÍFICOS DEL ROL (Zona / Detalles de Vehículo)
            if (userRole == UserRole.PASSENGER) {
                // ZONA DE RESIDENCIA
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    // enabled eliminado, se aplica al OutlinedTextField
                ) {
                    OutlinedTextField(
                        value = mainZone,
                        onValueChange = { },
                        label = { Text("Zona de Residencia*") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        enabled = !isLoading // Aplicado aquí
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        zones.forEach { zone -> DropdownMenuItem(text = { Text(zone) }, onClick = { mainZone = zone; expanded = false; focusManager.clearFocus() }) }
                    }
                }
            } else if (userRole == UserRole.DRIVER) {
                // DETALLES DEL VEHÍCULO
                OutlinedTextField(value = vehicleType, onValueChange = { vehicleType = onTextChange(it) }, label = { Text("Tipo de Vehículo*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), enabled = !isLoading)
                Spacer(Modifier.height(16.dp))
                // Número de Taxi (Solo numérico)
                OutlinedTextField(value = taxiNumber, onValueChange = {
                    val text = onTextChange(it)
                    if (text.all { char -> char.isDigit() }) { taxiNumber = text }
                }, label = { Text("Número de Taxi*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), enabled = !isLoading)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = onTextChange(it) }, label = { Text("Placa*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }), enabled = !isLoading)
            }
            Spacer(Modifier.height(16.dp))

            // CAMPO: NIP
            OutlinedTextField(
                value = nip,
                onValueChange = {
                    val text = onTextChange(it)
                    if (text.length <= 6 && text.all { char -> char.isDigit() }) { nip = text } // ✅ CORRECCIÓN: Permitir hasta 6
                    isNipError = text.length != 6 && text.isNotBlank() // ✅ CORRECCIÓN: Validar longitud 6
                },
                label = { Text("NIP de Acceso* (6 dígitos)") }, // ✅ ETIQUETA CORREGIDA
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { attemptSave() }),
                modifier = Modifier.fillMaxWidth(), singleLine = true, isError = isNipError,
                enabled = !isLoading
            )
            if (isNipError) {
                Text("El NIP debe ser de 6 dígitos.", color = MaterialTheme.colorScheme.error) // ✅ MENSAJE CORREGIDO
            }
            Spacer(Modifier.height(32.dp))

            // Mostrar error de guardado
            saveError?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            Button(
                onClick = { attemptSave() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading // Deshabilitar durante la carga
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
                } else {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}