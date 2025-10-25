package joyride.backend.dto.response

import kotlinx.serialization.Serializable

/**
 * DTO representing the Cost Per Kilometer (CPK) response for a car.
 * Provides efficiency, fuel type, fuel price, and calculated cost per kilometer.
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
