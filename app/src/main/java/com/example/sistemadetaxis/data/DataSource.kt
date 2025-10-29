package com.example.sistemadetaxis.data

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

// In-memory database simulation
object DataSource {

    // --- DRIVERS ---
    private val initialDrivers = listOf(
        TaxiDriver("1", "Juan Pérez", "5512345678", "Sedan", "A-123", "XYZ-789", "pass123", isAvailable = true),
        TaxiDriver("2", "Ana Torres", "5587654321", "Hatchback", "B-456", "ABC-123", "pass456", isAvailable = false)
    )
    val drivers = mutableStateListOf(*initialDrivers.toTypedArray())

    fun findDriverById(driverId: String): TaxiDriver? = drivers.find { it.id == driverId }

    fun findDriverByPhone(phone: String, password: String): TaxiDriver? = drivers.find { it.phoneNumber == phone && it.password == password }

    fun registerDriver(name: String, phone: String, vehicleType: String, taxiNumber: String, licensePlate: String, pass: String): TaxiDriver {
        val newDriver = TaxiDriver(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phone,
            vehicleType = vehicleType,
            taxiNumber = taxiNumber,
            licensePlate = licensePlate,
            password = pass
        )
        drivers.add(newDriver)
        return newDriver
    }

    fun toggleDriverAvailability(driverId: String) {
        findDriverById(driverId)?.let { driver ->
            val index = drivers.indexOf(driver)
            if (index != -1) {
                drivers[index] = driver.copy(isAvailable = !driver.isAvailable)
            }
        }
    }

    // --- PASSENGERS ---
    private val passengers = mutableStateListOf<Passenger>()

    fun findPassengerByEmail(email: String, password: String): Passenger? = passengers.find { it.email == email && it.password == password }

    fun registerPassenger(name: String, email: String, mainZone: String, pass: String): Passenger {
        val newPassenger = Passenger(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            mainZone = mainZone,
            password = pass
        )
        passengers.add(newPassenger)
        return newPassenger
    }
}
