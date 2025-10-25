package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * DTO for creating or updating a car.
 * Encapsulates all necessary properties for persisting a car
 * in the system or updating its existing data.
 */

@Serializable
data class CarCreateOrUpdateRequest(
    val ownerId: String? = null,
    val brand: String,
    val model: String,
    val buildYear: Int,
    val transmissionType: String,
    val color: String,
    val fuelType: String,
    val length: Int,
    val width: Int,
    val seats: Int,
    val isofixCompatible: Boolean,
    val phoneMount: Boolean,
    val luggageSpace: Double,
    val parkingSensors: Boolean,
    val locationX: Float,
    val locationY: Float,
    val licensePlate: String,
    val pricePerDay: Double,
    val purchasePrice: Double,
    val residualValue: Double,
    val usageYears: Int,
    val annualKm: Int,
    val energyCostPerKm: Double,
    val maintenanceCostPerKm: Double,
    val averageConsumption: Double
)