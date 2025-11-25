package com.example.sistemadetaxis.data

// Importaciones necesarias (si las tuvieras aquí)

data class Passenger(
    // Propiedades con valores por defecto (CRUCIAL para Firestore)
    val id: String = "",
    val name: String = "",
    val phone: String = "", // Número de teléfono (ID principal)
    val mainZone: String = "",
    val nip: String = ""  // NIP de acceso (Se usa como la contraseña)
) {
    // Constructor sin argumentos necesario para que Firebase Firestore pueda
    // crear una instancia de la clase al leer un documento.
    @Suppress("unused")
    private constructor() : this("", "", "", "", "")
}