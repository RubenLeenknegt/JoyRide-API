package leafcar.backend.domain

import kotlinx.serialization.Serializable

@Serializable
data class Car(
    val id: String,
    val ownerId: String,
    val brand: String,
    val model: String,
    val buildYear: Int,
    val transmissionType: TransmissionType,
    val color: Color,
    val fuelType: FuelType,
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
    val averageConsumption: Double,
) {
    // methodes
}