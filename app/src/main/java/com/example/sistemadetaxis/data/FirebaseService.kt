package com.example.sistemadetaxis.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.SetOptions
import com.example.sistemadetaxis.data.Passenger
import com.example.sistemadetaxis.data.TaxiDriver
import com.example.sistemadetaxis.data.UserRole


// ---------------------------------------------
// SERVICIO ASÍNCRONO DE FIREBASE (Reemplaza a DataSource)
// ---------------------------------------------

object FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Colecciones de Firestore
    private const val PASSENGERS_COLLECTION = "passengers"
    private const val DRIVERS_COLLECTION = "drivers"

    // --- UTILIDADES ---

    /** Convierte el teléfono a un formato de "email" para usar Firebase Auth. */
    private fun phoneToEmail(phone: String): String = "$phone@taxiapp.com"

    // -------------------------------------------------------------
    // FUNCIONES DE UNICIDAD (Usadas en EditProfileScreen)
    // -------------------------------------------------------------

    /** Verifica si un número de teléfono ya está registrado por otro PASAJERO (por el campo 'phone'). */
    suspend fun isPhoneRegisteredAsPassenger(phone: String): Boolean {
        return try {
            val snapshot = db.collection(PASSENGERS_COLLECTION)
                .whereEqualTo("phone", phone)
                .limit(1)
                .get().await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    /** Verifica si un número de teléfono ya está registrado por otro CONDUCTOR (por el campo 'phoneNumber'). */
    suspend fun isPhoneRegisteredAsDriver(phone: String): Boolean {
        return try {
            val snapshot = db.collection(DRIVERS_COLLECTION)
                .whereEqualTo("phoneNumber", phone)
                .limit(1)
                .get().await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }


    // --- REGISTRO ---

    /** Registra un nuevo Pasajero. */
    suspend fun registerPassenger(name: String, phone: String, mainZone: String, nip: String): String? {
        return try {
            val email = phoneToEmail(phone)
            val authResult = auth.createUserWithEmailAndPassword(email, nip).await()
            val userId = authResult.user?.uid ?: return null

            val newPassenger = Passenger(userId, name, phone, mainZone, nip)
            db.collection(PASSENGERS_COLLECTION).document(userId).set(newPassenger).await()
            userId
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    /** Registra un nuevo Conductor (isConfirmed = false por defecto). */
    suspend fun registerDriver(name: String, phone: String, vehicleType: String, taxiNumber: String, licensePlate: String, nip: String): String? {
        return try {
            val email = phoneToEmail(phone)
            val authResult = auth.createUserWithEmailAndPassword(email, nip).await()
            val userId = authResult.user?.uid ?: return null

            val newDriver = TaxiDriver(
                id = userId, name, phone, vehicleType, taxiNumber, licensePlate, nip,
                isConfirmed = false, isAvailable = false
            )
            db.collection(DRIVERS_COLLECTION).document(userId).set(newDriver).await()
            userId
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    // --- LOGIN / OBTENER DETALLES ---

    suspend fun signIn(phone: String, nip: String): String? {
        return try {
            val email = phoneToEmail(phone)
            val authResult = auth.signInWithEmailAndPassword(email, nip).await()
            authResult.user?.uid
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    /** Utilidad: Busca un conductor con credenciales válidas que NO esté confirmado (pendiente). */
    suspend fun findDriverByPhonePending(phone: String, nip: String): TaxiDriver? {
        return try {
            val email = phoneToEmail(phone)
            val authResult = auth.signInWithEmailAndPassword(email, nip).await()
            val userId = authResult.user?.uid ?: return null

            // Buscar en Firestore si existe y no está confirmado
            val snapshot = db.collection(DRIVERS_COLLECTION).document(userId).get().await()
            val driver = snapshot.toObject(TaxiDriver::class.java)

            if (driver != null && !driver.isConfirmed) {
                driver
            } else {
                null
            }
        } catch (e: Exception) { null }
    }

    suspend fun getPassengerDetails(userId: String): Passenger? {
        return try {
            val snapshot = db.collection(PASSENGERS_COLLECTION).document(userId).get().await()
            snapshot.toObject(Passenger::class.java)
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    suspend fun getDriverDetails(userId: String): TaxiDriver? {
        return try {
            val snapshot = db.collection(DRIVERS_COLLECTION).document(userId).get().await()
            snapshot.toObject(TaxiDriver::class.java)
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    // --- ACTUALIZACIÓN DE PERFIL ---

    /** Intenta actualizar el NIP (contraseña) del usuario actualmente logueado en Firebase Auth. */
    private suspend fun updateAuthNip(newNip: String): Boolean {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated.")
            user.updatePassword(newNip).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** Actualiza el perfil del pasajero en Firestore y, si es necesario, actualiza la contraseña de Auth. */
    suspend fun updatePassenger(id: String, fullName: String, phone: String, mainZone: String, nip: String): Boolean {
        return try {
            val authNipSuccess = updateAuthNip(nip)

            if (!authNipSuccess) throw Exception("Failed to update Auth password.")

            // Actualizar Firestore
            val updates = hashMapOf<String, Any>(
                "name" to fullName,
                "phone" to phone,
                "mainZone" to mainZone,
                "nip" to nip
            )
            db.collection(PASSENGERS_COLLECTION).document(id).update(updates).await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    /** Actualiza el perfil del conductor en Firestore y, si es necesario, actualiza la contraseña de Auth. */
    suspend fun updateDriver(id: String, fullName: String, phone: String, vehicleType: String, taxiNumber: String, licensePlate: String, nip: String): Boolean {
        return try {
            val authNipSuccess = updateAuthNip(nip)

            if (!authNipSuccess) throw Exception("Failed to update Auth password.")

            // Actualizar Firestore
            val updates = hashMapOf<String, Any>(
                "name" to fullName,
                "phoneNumber" to phone,
                "vehicleType" to vehicleType,
                "taxiNumber" to taxiNumber,
                "licensePlate" to licensePlate,
                "nip" to nip
            )
            db.collection(DRIVERS_COLLECTION).document(id).update(updates).await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    /** Actualiza la disponibilidad del conductor en Firestore */
    suspend fun toggleDriverAvailability(driverId: String, isAvailable: Boolean): Boolean {
        return try {
            val updates = hashMapOf<String, Any>("isAvailable" to isAvailable)
            db.collection(DRIVERS_COLLECTION).document(driverId).update(updates).await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    // --- ESTADO Y LOGOUT ---

    /** Restablece el NIP (contraseña) a través de Firebase Auth (envía un correo de reset). */
    suspend fun resetNipByPhone(phone: String): Boolean {
        return try {
            val email = phoneToEmail(phone)
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun signOut() { auth.signOut() }

    // --- FUNCIONES DE ADMINISTRACIÓN ---

    suspend fun getAllDrivers(): List<TaxiDriver> {
        return try {
            val snapshot = db.collection(DRIVERS_COLLECTION).get().await()
            snapshot.toObjects(TaxiDriver::class.java)
        } catch (e: Exception) { e.printStackTrace(); emptyList() }
    }

    suspend fun getAllPassengers(): List<Passenger> {
        return try {
            val snapshot = db.collection(PASSENGERS_COLLECTION).get().await()
            snapshot.toObjects(Passenger::class.java)
        } catch (e: Exception) { e.printStackTrace(); emptyList() }
    }

    suspend fun confirmDriver(driverId: String): Boolean {
        return try {
            val updates = hashMapOf<String, Any>("isConfirmed" to true, "isAvailable" to true)
            db.collection(DRIVERS_COLLECTION).document(driverId).update(updates).await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    suspend fun removeDriver(driverId: String): Boolean {
        return try {
            db.collection(DRIVERS_COLLECTION).document(driverId).delete().await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    suspend fun removePassenger(passengerId: String): Boolean {
        return try {
            db.collection(PASSENGERS_COLLECTION).document(passengerId).delete().await()
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }
}