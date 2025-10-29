package com.example.sistemadetaxis.data

data class Passenger(
    val id: String,
    val name: String,
    val email: String,
    val mainZone: String,
    val password: String // Added for login
)
