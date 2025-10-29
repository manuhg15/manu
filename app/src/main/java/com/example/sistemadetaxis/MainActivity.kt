package com.example.sistemadetaxis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sistemadetaxis.data.UserRole
import com.example.sistemadetaxis.screens.*
import com.example.sistemadetaxis.ui.theme.SistemaDeTaxisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

@Composable
fun TaxiApp() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Inicio", "Acceder", "Servicio")

    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }
    var loggedInUserId by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var pendingTabChange by remember { mutableStateOf<Int?>(null)
    }

    fun onLogout(andThen: () -> Unit = {}) {
        currentUserRole = null
        loggedInUserId = null
        // After logout, always go to the Acceder tab
        selectedTab = 1
        navController.navigate("login_flow_start") {
            popUpTo(navController.graph.startDestinationId)
        }
        andThen()
    }

    fun handleTabClick(index: Int) {
        // If user is logged in (on service tab) and clicks another tab
        if (selectedTab == 2 && currentUserRole != null && index != 2) {
            pendingTabChange = index
            showLogoutDialog = true
        } 
        // If user is not logged in and clicks service tab, redirect to login
        else if (index == 2 && currentUserRole == null) {
            handleTabClick(1) // Go to Acceder tab
        } 
        // Otherwise, navigate normally
        else {
            selectedTab = index
            val route = when (index) {
                0 -> "home"
                1 -> "login_flow_start"
                else -> "main_content"
            }
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                Button({
                    showLogoutDialog = false
                    onLogout { 
                        pendingTabChange?.let { handleTabClick(it) }
                        pendingTabChange = null
                    } 
                }) { Text("Sí, Salir") }
            },
            dismissButton = {
                Button({ showLogoutDialog = false; pendingTabChange = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { handleTabClick(index) },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(onIconClick = { handleTabClick(1) }) }
            composable("login_flow_start") { LoginScreen(onRoleSelected = { role -> navController.navigate("auth_choice/$role") }) }

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
                RegistrationScreen(role = role, onRegisterSuccess = { navController.navigate("sign_in/$role") }, onBackClick = { navController.popBackStack() })
            }

            composable("sign_in/{role}", arguments = listOf(navArgument("role") { type = NavType.StringType })) { backStack ->
                val role = backStack.arguments?.getString("role")!!
                SignInScreen(
                    role = role, 
                    onSignInSuccess = { userId -> 
                        currentUserRole = if (role == "passenger") UserRole.PASSENGER else UserRole.DRIVER
                        loggedInUserId = userId
                        handleTabClick(2) // Navigate to service tab
                    }, 
                    onBackClick = { navController.popBackStack() })
            }

            composable("profile") {
                if (loggedInUserId != null && currentUserRole != null) {
                    ProfileScreen(userId = loggedInUserId!!, userRole = currentUserRole!!, onBackClick = { navController.popBackStack() })
                }
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