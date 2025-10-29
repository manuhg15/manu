package com.example.sistemadetaxis.data

data class TaxiDriver(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val vehicleType: String,
    val taxiNumber: String,
    val licensePlate: String,
    val password: String, // Added for login
    var isAvailable: Boolean = true
)
