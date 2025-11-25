package com.example.sistemadetaxis.data

// Importaciones necesarias (si las tuvieras aquí)

data class TaxiDriver(
    // Propiedades con valores por defecto (CRUCIAL para Firestore)
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "", // Número de teléfono
    val vehicleType: String = "",
    val taxiNumber: String = "",
    val licensePlate: String = "",
    val nip: String = "", // NIP de acceso
    val isAvailable: Boolean = false, // Estado de disponibilidad
    val isConfirmed: Boolean = false // Estado de aprobación del Admin
) {
    // Constructor sin argumentos necesario para que Firebase Firestore pueda
    // crear una instancia de la clase al leer un documento.
    @Suppress("unused")
    private constructor() : this("", "", "", "", "", "", "", false, false)
}