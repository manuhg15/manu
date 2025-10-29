package com.example.sistemadetaxis.data

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

// In-memory database simulation
object DataSource {

    // --- DRIVERS ---
    private val initialDrivers = listOf(
        TaxiDriver("d1", "Juan Perez Garcia", "5511223344", "Sedan", "A-101", "NNS-34-54", "pass123", isAvailable = true),
        TaxiDriver("d2", "Ana Torres Lopez", "5522334455", "Hatchback", "B-202", "GTR-33-11", "pass456", isAvailable = false),
        TaxiDriver("d3", "Carlos Sanchez Martinez", "5533445566", "SUV", "C-303", "FRS-98-34", "pass789"),
        TaxiDriver("d4", "Sofia Lopez Hernandez", "5544556677", "Tsuru", "D-404", "GHT-56-12", "pass101"),
        TaxiDriver("d5", "Miguel Hernandez Gonzalez", "5555667788", "Versa", "E-505", "YTR-33-21", "pass112"),
        TaxiDriver("d6", "Laura Garcia Rodriguez", "5566778899", "Sentra", "F-606", "BVC-68-33", "pass131"),
        TaxiDriver("d7", "David Ramirez Perez", "5577889900", "Tsuru", "G-707", "UYT-89-66", "pass141"),
        TaxiDriver("d8", "Valeria Martinez Sanchez", "5588990011", "Aveo", "H-808", "POU-43-11", "pass151", isAvailable = true)
    )
    val drivers = mutableStateListOf(*initialDrivers.toTypedArray())

    fun findDriverById(driverId: String): TaxiDriver? = drivers.find { it.id == driverId }
    fun getDriver(driverId: String): TaxiDriver? = findDriverById(driverId)
    fun findDriverByPhone(phone: String, password: String): TaxiDriver? = drivers.find { it.phoneNumber == phone && it.password == password }
    fun removeDriver(driver: TaxiDriver) = drivers.remove(driver)

    fun registerDriver(name: String, phone: String, vehicleType: String, taxiNumber: String, licensePlate: String, pass: String): TaxiDriver {
        val newDriver = TaxiDriver(
            id = "d" + UUID.randomUUID().toString(), name, phone, vehicleType, taxiNumber, licensePlate, pass
        )
        drivers.add(newDriver)
        return newDriver
    }

    fun updateDriver(id: String, name: String, phone: String, vehicleType: String, taxiNumber: String, licensePlate: String) {
        val index = drivers.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldDriver = drivers[index]
            drivers[index] = oldDriver.copy(
                name = name,
                phoneNumber = phone,
                vehicleType = vehicleType,
                taxiNumber = taxiNumber,
                licensePlate = licensePlate
            )
        }
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
        Passenger("p1", "Lucia Gomez Flores", "lucia@email.com", "Centro", "pass1"),
        Passenger("p2", "Marcos Jimenez Gonzalez", "marcos@email.com", "San Sebastián", "pass2"),
        Passenger("p3", "Elena Vazquez Perez", "elena@email.com", "Santa Anita", "pass3"),
        Passenger("p4", "Ricardo Nava Torres", "ricardo@email.com", "La Trinidad", "pass4"),
        Passenger("p5", "Beatriz Morales Lopez", "beatriz@email.com", "El Carmen", "pass5"),
        Passenger("p6", "Ivan Rojas Hernandez", "ivan@email.com", "Guadalupe", "pass6"),
        Passenger("p7", "Silvia Castillo Martinez", "silvia@email.com", "San Francisco Yancuitlalpan", "pass7"),
        Passenger("p8", "Raul Ortiz Sanchez", "raul@email.com", "San José Xicohténcatl", "pass8")
    )
    val passengers = mutableStateListOf(*initialPassengers.toTypedArray())

    fun findPassengerById(passengerId: String): Passenger? = passengers.find { it.id == passengerId }
    fun getPassenger(passengerId: String): Passenger? = findPassengerById(passengerId)
    fun findPassengerByEmail(email: String, password: String): Passenger? = passengers.find { it.email == email && it.password == password }
    fun removePassenger(passenger: Passenger) = passengers.remove(passenger)

    fun registerPassenger(name: String, email: String, mainZone: String, pass: String): Passenger {
        val newPassenger = Passenger(
            id = "p" + UUID.randomUUID().toString(), name, email, mainZone, pass
        )
        passengers.add(newPassenger)
        return newPassenger
    }

    fun updatePassenger(id: String, name: String, email: String, mainZone: String) {
        val index = passengers.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldPassenger = passengers[index]
            passengers[index] = oldPassenger.copy(
                name = name,
                email = email,
                mainZone = mainZone
            )
        }
    }
}