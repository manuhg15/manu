package com.example.sistemadetaxis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.* import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sistemadetaxis.data.UserRole
// Importa todas las pantallas necesarias desde la carpeta screens
import com.example.sistemadetaxis.screens.* import com.example.sistemadetaxis.ui.theme.SistemaDeTaxisTheme
import kotlinx.coroutines.launch // Necesario para popUpTo

// Definiciones de colores
val GreenButtonColor = Color(0xFF4CAF50)
val PurpleButtonColor = Color(0xFF673AB7)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración para el modo Fullscreen o Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            SistemaDeTaxisTheme {
                TaxiApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxiApp() {
    val navController = rememberNavController()

    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }
    var loggedInUserId by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // Necesario si usas scope.launch para popUpTo, aunque la versión actual no lo necesita.


    fun onLogout() {
        currentUserRole = null
        loggedInUserId = null
        // Navega al inicio (pantalla de bienvenida) y limpia el historial
        navController.navigate("home") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    // Obtiene la ruta actual para el TopAppBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Función para generar el título y el modo (Título fijo)
    fun getAppBarTitleAndMode(): Pair<String, String?> {
        val modeText = when (currentUserRole) {
            UserRole.PASSENGER -> "MODO: PASAJERO"
            UserRole.DRIVER -> "MODO: CONDUCTOR"
            else -> null
        }

        // Título Fijo
        val title = "Consultoria de Taxis"

        return Pair(title, modeText)
    }

    val (appBarTitle, modeText) = getAppBarTitleAndMode()

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                Button({ showLogoutDialog = false; onLogout() }) { Text("Sí, Salir") }
            },
            dismissButton = {
                Button({ showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            // **TOPAPPBAR DINÁMICO**
            TopAppBar(
                title = {
                    Text(
                        text = appBarTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                // Etiqueta de MODO en la derecha
                actions = {
                    modeText?.let {
                        Text(
                            text = it,
                            modifier = Modifier.padding(end = 16.dp),
                            color = if (currentUserRole == UserRole.PASSENGER) GreenButtonColor else PurpleButtonColor,
                            fontSize = 14.sp
                        )
                    }
                },
                // Botón de Regresar/Navegación
                navigationIcon = {
                    // Muestra el botón de regreso si NO estamos en la raíz o en pantallas iniciales
                    val showBack = navController.previousBackStackEntry != null &&
                            currentRoute != "home" &&
                            currentRoute != "login_flow_start" &&
                            currentRoute != "main_content"

                    if (showBack) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    snackbarData = data,
                    containerColor = Color(0xFF303030),
                    contentColor = Color.White
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                // Al hacer clic en el ícono, navega a la selección de rol
                HomeScreen(onIconClick = { navController.navigate("login_flow_start") })
            }

            composable("login_flow_start") {
                LoginScreen(
                    onRoleSelected = { role ->
                        navController.navigate("auth_choice/$role")
                    },
                    onAdminClick = { navController.navigate("admin_login") }
                )
            }

            composable("admin_login") {
                AdminLoginScreen(onAdminLoginSuccess = { navController.navigate("admin_dashboard") })
            }

            // ✅ CORRECCIÓN: ELIMINAR PARÁMETROS onAddPassenger y onAddDriver
            composable("admin_dashboard") {
                AdminDashboardScreen(
                    onLogout = { onLogout() }
                )
            }

            composable("auth_choice/{role}", arguments = listOf(navArgument("role") { type = NavType.StringType })) { backStack ->
                val role = backStack.arguments?.getString("role")!!
                AuthChoiceScreen(
                    onNavigateToSignIn = { navController.navigate("sign_in/$role") },
                    onNavigateToRegister = { navController.navigate("register/$role") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("register/{role}", arguments = listOf(navArgument("role") { type = NavType.StringType })) { backStack ->
                val role = backStack.arguments?.getString("role")!!
                RegistrationScreen(
                    role = role,
                    // Después de registrar, se redirige a la pantalla de Iniciar Sesión para el rol
                    onRegisterSuccess = { navController.navigate("sign_in/$role") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("sign_in/{role}", arguments = listOf(navArgument("role") { type = NavType.StringType })) { backStack ->
                val role = backStack.arguments?.getString("role")!!
                SignInScreen(
                    role = role,
                    onSignInSuccess = { userId ->
                        currentUserRole = if (role == "passenger") UserRole.PASSENGER else UserRole.DRIVER
                        loggedInUserId = userId
                        // Después del login, navegar directamente al servicio (MainContentScreen)
                        navController.navigate("main_content") {
                            // Limpia el historial hasta 'home' e incluye 'home'
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBackClick = { navController.popBackStack() },
                    // ✅ NUEVO ARGUMENTO: Navegación a recuperar NIP
                    onRecoverNipClick = { r -> navController.navigate("recover_nip/$r") }
                )
            }

            // ✅ NUEVA COMPOSABLE: Ruta para Recuperar NIP
            composable("recover_nip/{role}", arguments = listOf(navArgument("role") { type = NavType.StringType })) { backStack ->
                val role = backStack.arguments?.getString("role")!!
                RecoverNipScreen(
                    role = role,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("profile") {
                if (loggedInUserId != null && currentUserRole != null) {
                    ProfileScreen(
                        userId = loggedInUserId!!,
                        userRole = currentUserRole!!,
                        onBackClick = { navController.popBackStack() },
                        onEditProfile = { userId, userRole -> navController.navigate("edit_profile/$userId/${userRole.name}") }
                    )
                }
            }

            composable(
                "edit_profile/{userId}/{userRole}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("userRole") { type = NavType.StringType }
                )
            ) {
                val userId = it.arguments?.getString("userId")!!
                val userRole = UserRole.valueOf(it.arguments?.getString("userRole")!!)
                EditProfileScreen(
                    userId = userId,
                    userRole = userRole,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { navController.popBackStack() }
                )
            }

            composable("main_content") {
                MainContentScreen(
                    currentUserRole = currentUserRole,
                    loggedInUserId = loggedInUserId,
                    onLogout = { showLogoutDialog = true },
                    onViewProfile = { navController.navigate("profile") }
                )
            }
        }
    }
}