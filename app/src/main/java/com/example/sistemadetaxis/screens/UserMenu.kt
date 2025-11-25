package com.example.sistemadetaxis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert // Se mantiene para compatibilidad con algunos IDEs
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Definición de colores para el menú
val ProfileMenuColor = Color(0xFF2196F3) // Azul para Ver Perfil
val LogoutMenuColor = Color(0xFFF44336)  // Rojo para Salir

@Composable
fun UserMenu(onViewProfile: () -> Unit, onLogout: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    // Usamos Box con wrapContentSize para asegurar que el menú se ancle al ícono
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            // ✅ Usamos el ícono de tres líneas horizontales (Menu)
            Icon(
                Icons.Filled.Menu,
                contentDescription = "Menú de usuario"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // 1. VER PERFIL (Fondo Azul)
            DropdownMenuItem(
                text = {
                    // El texto es blanco para contrastar con el fondo azul
                    Text("Ver Perfil", color = Color.White)
                },
                onClick = {
                    expanded = false
                    onViewProfile()
                },
                modifier = Modifier.background(ProfileMenuColor)
            )

            // 2. SALIR (Fondo Rojo)
            DropdownMenuItem(
                text = {
                    // El texto es blanco para contrastar con el fondo rojo
                    Text("Salir", color = Color.White)
                },
                onClick = {
                    expanded = false
                    onLogout()
                },
                modifier = Modifier.background(LogoutMenuColor)
            )
        }
    }
}