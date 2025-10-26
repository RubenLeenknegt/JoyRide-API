package joyride.backend.dto.response

import kotlinx.serialization.Serializable

/**
 * Response DTO representing Cost Per Kilometer (CPK) data for a car.
 *
 * Contains identifiers for the car and its owner, fuel consumption and price
 * information, the fuel type, and the computed cost per kilometer.
 *
 * @property id Identifier of the car.
 * @property ownerId Identifier of the car owner.
 * @property averageConsumption Average fuel consumption (e.g., liters per 100 km).
 * @property fuelType Fuel type label (for example: `petrol`, `diesel`, `electric`).
 * @property fuelPrice Current fuel price per unit (e.g., per liter or per kWh).
 * @property cpk Calculated cost per kilometer in the same currency as `fuelPrice`.
 */

@Serializable
data class CarCpkDataResponse(
    val id: String,
    val ownerId: String,
    val averageConsumption: Double,
    val fuelType: String,
    val fuelPrice: Double,
    val cpk: Double
)
