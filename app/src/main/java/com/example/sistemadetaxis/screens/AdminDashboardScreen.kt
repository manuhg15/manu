package com.example.sistemadetaxis.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadetaxis.data.FirebaseService
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.TaxiDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit
    // Se eliminan onAddPassenger y onAddDriver
) {
    // ✅ ESTADOS DE DATOS REALES DE FIREBASE
    var allPassengers by remember { mutableStateOf<List<Passenger>>(emptyList()) }
    var allDrivers by remember { mutableStateOf<List<TaxiDriver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pasajeros", "Conductores")
    var searchText by remember { mutableStateOf("") }
    var searchType by remember { mutableStateOf("name") }

    // Función para recargar ambas listas de Firebase
    fun reloadLists() {
        scope.launch {
            isLoading = true
            allPassengers = FirebaseService.getAllPassengers() // Llamada asíncrona a Firestore
            allDrivers = FirebaseService.getAllDrivers()       // Llamada asíncrona a Firestore
            isLoading = false
        }
    }

    // ✅ CARGA ASÍNCRONA DE AMBAS LISTAS AL INICIO
    LaunchedEffect(Unit) {
        reloadLists()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Panel de Administración") },
                    actions = {
                        Button(
                            onClick = { FirebaseService.signOut(); onLogout() }, // Cierre de sesión real
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43336))
                        ) {
                            Text("Salir", color = Color.White)
                        }
                    }
                )
                // PESTAÑAS DE NAVEGACIÓN
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                searchText = ""
                                searchType = if (index == 0) "name" else "name"
                            },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        // Se elimina el floatingActionButton
        floatingActionButton = { /* Se elimina el FAB */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // LÓGICA DE BÚSQUEDA Y FILTRADO
            SearchAndFilterBar(
                selectedTabIndex = selectedTabIndex,
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                searchType = searchType,
                onSearchTypeChange = { searchType = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTabIndex) {
                    // ✅ PASAR LISTAS ESTATALES, FUNCIÓN DE RECARGA Y SCOPE
                    0 -> PassengerList(allPassengers, searchText, searchType, scope, ::reloadLists)
                    1 -> DriverList(allDrivers, searchText, searchType, scope, ::reloadLists)
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// COMPONENTES DE AYUDA
// ----------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterBar(
    selectedTabIndex: Int,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    searchType: String,
    onSearchTypeChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = if (selectedTabIndex == 0) {
        listOf("name" to "Nombre", "zone" to "Zona")
    } else {
        listOf("name" to "Nombre", "taxi" to "Num. Taxi", "plate" to "Placa")
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            label = { Text("Buscar") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box {
            Button(onClick = { expanded = true }) {
                Text(options.find { it.first == searchType }?.second ?: "Filtro")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSearchTypeChange(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


// ----------------------------------------------------------------------------------
// LISTAS Y TARJETAS (Funcionalidad de Firebase en Corrutinas)
// ----------------------------------------------------------------------------------

@Composable
fun PassengerList(passengers: List<Passenger>, searchText: String, searchType: String, scope: CoroutineScope, reloadLists: () -> Unit) {

    val filteredPassengers = remember(passengers, searchText, searchType) {
        passengers
            .filter { passenger ->
                if (searchText.isBlank()) return@filter true
                val query = searchText.lowercase()
                when (searchType) {
                    "name" -> passenger.name.lowercase().contains(query)
                    "zone" -> passenger.mainZone.lowercase().contains(query)
                    else -> true
                }
            }
            .sortedBy { it.name }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Total: ${filteredPassengers.size} pasajeros", modifier = Modifier.padding(vertical = 8.dp)) }
        items(filteredPassengers, key = { it.id }) { passenger ->
            PassengerInfoCard(passenger, scope, reloadLists)
        }
    }
}

@Composable
fun PassengerInfoCard(passenger: Passenger, scope: CoroutineScope, reloadLists: () -> Unit) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${passenger.name}", fontWeight = FontWeight.Bold)
            Text("Teléfono: ${passenger.phone}")
            Text("Zona: ${passenger.mainZone}")

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showConfirmDelete = true }, // Abre el diálogo
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43336)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Eliminar", color = Color.White)
            }
        }
    }

    // ⚠️ Diálogo de Confirmación de ELIMINACIÓN de Pasajero
    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar a ${passenger.name} del sistema? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch { // ✅ ELIMINACIÓN ASÍNCRONA
                            FirebaseService.removePassenger(passenger.id) // Usar el ID
                            reloadLists() // Recargar para reflejar el cambio
                            showConfirmDelete = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43336))
                ) {
                    Text("Sí, Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDelete = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DriverList(drivers: List<TaxiDriver>, searchText: String, searchType: String, scope: CoroutineScope, reloadLists: () -> Unit) {
    val filteredDrivers = remember(drivers, searchText, searchType) {
        drivers
            .filter { driver ->
                if (searchText.isBlank()) return@filter true
                val query = searchText.lowercase()
                when (searchType) {
                    "name" -> driver.name.lowercase().contains(query)
                    "taxi" -> driver.taxiNumber.lowercase().contains(query)
                    "plate" -> driver.licensePlate.lowercase().contains(query)
                    else -> true
                }
            }
            .sortedBy { it.name }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Total: ${filteredDrivers.size} conductores", modifier = Modifier.padding(vertical = 8.dp)) }
        items(filteredDrivers, key = { it.id }) { driver ->
            DriverApprovalCard(driver, scope, reloadLists)
        }
    }
}

@Composable
fun DriverApprovalCard(driver: TaxiDriver, scope: CoroutineScope, reloadLists: () -> Unit) {
    var showSmsIntent by remember { mutableStateOf(false) }
    var showConfirmAccept by remember { mutableStateOf(false) } // Estado para confirmar aceptación
    var showConfirmReject by remember { mutableStateOf(false) } // Estado para confirmar rechazo
    val context = LocalContext.current // Obtenemos el contexto aquí

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${driver.name}", fontWeight = FontWeight.Bold)
            Text("Teléfono: ${driver.phoneNumber}")
            Text("Vehículo: ${driver.vehicleType}")
            Text("No. Taxi: ${driver.taxiNumber}")
            Text("Placa: ${driver.licensePlate}")
            Text("Estado: ${if (driver.isConfirmed) "Aprobado" else "Pendiente"}",
                color = if (driver.isConfirmed) Color(0xFF4CAF50) else Color(0xFFFFA000),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!driver.isConfirmed) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { showConfirmReject = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43336))
                    ) {
                        Text("Rechazar")
                    }

                    Button(
                        onClick = { showConfirmAccept = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Aprobar")
                    }
                }
            } else {
                Button(
                    onClick = { showConfirmReject = true }, // Usa el diálogo de rechazo/eliminación
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43336)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }

    // ⚠️ Diálogo de Confirmación de ACEPTACIÓN
    if (showConfirmAccept) {
        AlertDialog(
            onDismissRequest = { showConfirmAccept = false },
            title = { Text("Confirmar Conductor") },
            text = { Text("¿Estás seguro de que deseas ACEPTAR a ${driver.name}? Esto le dará acceso total como conductor y se le enviará un SMS de confirmación.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch { // ✅ ACEPTACIÓN ASÍNCRONA
                            FirebaseService.confirmDriver(driver.id)
                            reloadLists() // Recargar para reflejar el cambio
                            showSmsIntent = true // Activa el Intent de SMS
                            showConfirmAccept = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Sí, Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmAccept = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ⚠️ Diálogo de Confirmación de RECHAZO/ELIMINACIÓN
    if (showConfirmReject) {
        AlertDialog(
            onDismissRequest = { showConfirmReject = false },
            title = { Text("Confirmar Acción") },
            text = { Text("¿Estás seguro de que quieres ${if (driver.isConfirmed) "eliminar" else "rechazar"} a ${driver.name}? Esta acción es permanente.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch { // ✅ RECHAZO ASÍNCRONO
                            FirebaseService.removeDriver(driver.id) // Usar driver.id
                            reloadLists() // Recargar para reflejar el cambio
                            showConfirmReject = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43336))
                ) {
                    Text(if (driver.isConfirmed) "Sí, Eliminar" else "Sí, Rechazar")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmReject = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ✅ CORRECCIÓN CLAVE: Lógica de SMS consolidada
    LaunchedEffect(showSmsIntent) {
        if (showSmsIntent) {
            val message = "¡Felicidades! Tu registro como conductor en Consultoria de Taxis ha sido aprobado. Ya puedes iniciar sesión en la aplicación."

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${driver.phoneNumber}")
                putExtra("sms_body", message)
            }
            context.startActivity(intent)

            // IMPORTANTE: Resetear el estado de la bandera inmediatamente después de lanzar el Intent.
            showSmsIntent = false
        }
    }
}

// ----------------------------------------------------------------------------------
// UTILITY SMS (Se mantiene aquí por referencia, pero su lógica fue absorbida por DriverApprovalCard)
// ----------------------------------------------------------------------------------

/**
 * Función que lanza un Intent para abrir la app de SMS con un mensaje precargado.
 */
@Composable
fun SmsConfirmationIntent(driverPhoneNumber: String) {
    val context = LocalContext.current
    val message = "¡Felicidades! Tu registro como conductor en Consultoria de Taxis ha sido aprobado. Ya puedes iniciar sesión en la aplicación."

    LaunchedEffect(driverPhoneNumber) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${driverPhoneNumber}") // Establece el número
            putExtra("sms_body", message) // Precarga el cuerpo del mensaje
        }
        context.startActivity(intent)
    }
}