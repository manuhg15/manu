package com.example.sistemadetaxis.data

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

// In-memory database simulation
object DataSource {

    // --- DRIVERS ---
    private val initialDrivers = listOf(
        TaxiDriver("d1", "Juan Pérez", "5511223344", "Sedan", "A-101", "NNS-34-54", "pass123", isAvailable = true),
        TaxiDriver("d2", "Ana Torres", "5522334455", "Hatchback", "B-202", "GTR-33-11", "pass456", isAvailable = false),
        TaxiDriver("d3", "Carlos Sanchez", "5533445566", "SUV", "C-303", "FRS-98-34", "pass789"),
        TaxiDriver("d4", "Sofia Lopez", "5544556677", "Tsuru", "D-404", "GHT-56-12", "pass101"),
        TaxiDriver("d5", "Miguel Hernandez", "5555667788", "Versa", "E-505", "YTR-33-21", "pass112"),
        TaxiDriver("d6", "Laura Garcia", "5566778899", "Sentra", "F-606", "BVC-68-33", "pass131"),
        TaxiDriver("d7", "David Ramirez", "5577889900", "Tsuru", "G-707", "UYT-89-66", "pass141"),
        TaxiDriver("d8", "Valeria Martinez", "5588990011", "Aveo", "H-808", "POU-43-11", "pass151", isAvailable = true),
        TaxiDriver("d9", "Javier Rodriguez", "5599001122", "Sedan", "I-909", "MNB-99-43", "pass161"),
        TaxiDriver("d10", "Fernanda Diaz", "5500112233", "SUV", "J-101", "VFR-54-98", "pass171", isAvailable = true)
    )
    val drivers = mutableStateListOf(*initialDrivers.toTypedArray())

    fun findDriverById(driverId: String): TaxiDriver? = drivers.find { it.id == driverId }
    fun findDriverByPhone(phone: String, password: String): TaxiDriver? = drivers.find { it.phoneNumber == phone && it.password == password }
    fun removeDriver(driver: TaxiDriver) = drivers.remove(driver)

    fun registerDriver(name: String, phone: String, vehicleType: String, taxiNumber: String, licensePlate: String, pass: String): TaxiDriver {
        val newDriver = TaxiDriver(
            id = "d" + UUID.randomUUID().toString(), name, phone, vehicleType, taxiNumber, licensePlate, pass
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
    private val initialPassengers = listOf(
        Passenger("p1", "Lucia Gomez", "lucia@email.com", "Centro", "pass1"),
        Passenger("p2", "Marcos Jimenez", "marcos@email.com", "La Candelaria", "pass2"),
        Passenger("p3", "Elena Vazquez", "elena@email.com", "San Jose", "pass3"),
        Passenger("p4", "Ricardo Nava", "ricardo@email.com", "El Carmen", "pass4"),
        Passenger("p5", "Beatriz Morales", "beatriz@email.com", "Centro", "pass5"),
        Passenger("p6", "Ivan Rojas", "ivan@email.com", "San Bartolome", "pass6"),
        Passenger("p7", "Silvia Castillo", "silvia@email.com", "La Candelaria", "pass7"),
        Passenger("p8", "Raul Ortiz", "raul@email.com", "San Jose", "pass8"),
        Passenger("p9", "Gloria Mendoza", "gloria@email.com", "El Carmen", "pass9"),
        Passenger("p10", "Hector Flores", "hector@email.com", "Centro", "pass10")
    )
    val passengers = mutableStateListOf(*initialPassengers.toTypedArray())

    fun findPassengerById(passengerId: String): Passenger? = passengers.find { it.id == passengerId }
    fun findPassengerByEmail(email: String, password: String): Passenger? = passengers.find { it.email == email && it.password == password }
    fun removePassenger(passenger: Passenger) = passengers.remove(passenger)

    fun registerPassenger(name: String, email: String, mainZone: String, pass: String): Passenger {
        val newPassenger = Passenger(
            id = "p" + UUID.randomUUID().toString(), name, email, mainZone, pass
        )
        passengers.add(newPassenger)
        return newPassenger
    }
}