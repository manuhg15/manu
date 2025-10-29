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
import androidx.compose.ui.unit.dp
import com.example.sistemadetaxis.data.DataSource
import com.example.sistemadetaxis.data.TaxiDriver
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: String,
    userRole: UserRole,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val user = when (userRole) {
        UserRole.PASSENGER -> DataSource.getPassenger(userId)
        UserRole.DRIVER -> DataSource.getDriver(userId)
    }

    var firstName by remember { mutableStateOf("") }
    var paternalLastName by remember { mutableStateOf("") }
    var maternalLastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var isPhoneError by remember { mutableStateOf(false) }
    var mainZone by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var taxiNumber by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }

    val zones = listOf("Centro", "San Sebastián", "Santa Anita", "La Trinidad", "El Carmen", "Guadalupe", "San Francisco Yancuitlalpan", "San José Xicohténcatl")
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        if (user != null) {
            val currentName = when (user) {
                is Passenger -> user.name
                is TaxiDriver -> user.name
                else -> ""
            }
            val nameParts = currentName.split(" ")
            firstName = nameParts.getOrNull(0) ?: ""
            paternalLastName = nameParts.getOrNull(1) ?: ""
            maternalLastName = nameParts.getOrNull(2) ?: ""
            if (user is Passenger) {
                email = user.email
                mainZone = user.mainZone
            }
            if (user is TaxiDriver) {
                phone = user.phoneNumber
                vehicleType = user.vehicleType
                taxiNumber = user.taxiNumber
                licensePlate = user.licensePlate
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePhone(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
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
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = paternalLastName, onValueChange = { paternalLastName = it }, label = { Text("Apellido Paterno") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = maternalLastName, onValueChange = { maternalLastName = it }, label = { Text("Apellido Materno") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            if (userRole == UserRole.PASSENGER) {
                OutlinedTextField(
                    value = email, 
                    onValueChange = { 
                        email = it 
                        isEmailError = !validateEmail(it)
                    }, 
                    label = { Text("Correo Electrónico") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), 
                    modifier = Modifier.fillMaxWidth(),
                    isError = isEmailError
                )
                if (isEmailError) {
                    Text("Por favor, introduce un correo válido.", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = mainZone,
                        onValueChange = { },
                        label = { Text("Zona de Residencia") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        zones.forEach { zone ->
                            DropdownMenuItem(
                                text = { Text(zone) },
                                onClick = {
                                    mainZone = zone
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = phone, 
                    onValueChange = { 
                        phone = it 
                        isPhoneError = !validatePhone(it)
                    }, 
                    label = { Text("Número de Teléfono") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), 
                    modifier = Modifier.fillMaxWidth(),
                    isError = isPhoneError
                )
                if (isPhoneError) {
                    Text("El teléfono debe tener 10 dígitos.", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = vehicleType, onValueChange = { vehicleType = it }, label = { Text("Tipo de Vehículo") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = taxiNumber, onValueChange = { taxiNumber = it }, label = { Text("Número de Taxi") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = it }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    val fullName = "$firstName $paternalLastName $maternalLastName"
                    if (userRole == UserRole.PASSENGER) {
                        if (!isEmailError && mainZone.isNotEmpty()) {
                            DataSource.updatePassenger(userId, fullName, email, mainZone)
                            onSaveClick()
                        }
                    } else {
                        if (!isPhoneError) {
                            DataSource.updateDriver(userId, fullName, phone, vehicleType, taxiNumber, licensePlate)
                            onSaveClick()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = if (userRole == UserRole.PASSENGER) !isEmailError && mainZone.isNotEmpty() else !isPhoneError
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}