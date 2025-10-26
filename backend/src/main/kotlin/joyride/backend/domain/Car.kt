package joyride.backend.domain

import kotlinx.serialization.Serializable

/**
 * Domain model representing a car within the application.
 *
 * Serves as the main data structure exchanged between services,
 * repositories, and API layers. Serializable for API and storage use.
 */

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
)

/**
 * @property id Unique identifier of the car.
 * @property ownerId Identifier of the user who owns the car.
 * @property brand Car brand (e.g., Tesla, BMW).
 * @property model Specific model name of the car.
 * @property buildYear Year the car was manufactured.
 * @property transmissionType Type of transmission (manual or automatic).
 * @property color Car color.
 * @property fuelType Type of fuel used (e.g., electric, petrol, diesel).
 * @property length Car length in centimeters.
 * @property width Car width in centimeters.
 * @property seats Number of available seats.
 * @property isofixCompatible Whether the car supports ISOFIX child seats.
 * @property phoneMount Whether the car is equipped with a phone mount.
 * @property luggageSpace Trunk volume in liters.
 * @property parkingSensors Whether the car has parking sensors.
 * @property locationX X-coordinate representing car’s location.
 * @property locationY Y-coordinate representing car’s location.
 * @property licensePlate Official license plate number.
 * @property pricePerDay Daily rental price.
 * @property purchasePrice Original purchase price of the car.
 * @property residualValue Estimated residual value after depreciation.
 * @property usageYears Number of years the car has been in use.
 * @property annualKm Average annual distance driven in kilometers.
 * @property energyCostPerKm Energy cost per kilometer driven.
 * @property maintenanceCostPerKm Maintenance cost per kilometer driven.
 * @property averageConsumption Average energy or fuel consumption per 100 km.
 */
