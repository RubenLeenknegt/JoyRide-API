package leafcar.backend.dto.response

import kotlinx.serialization.Serializable

/**
 * DTO representing the Total Cost of Ownership (TCO) response for a car.
 * Provides cost-related metrics and the calculated total ownership cost.
 */

@Serializable
data class CarTcoDataResponse(
    val id: String,
    val purchasePrice: Double,
    val residualValue: Double,
    val usageYears: Int,
    val annualKm: Int,
    val energyCostPerKm: Double,
    val maintenanceCostPerKm: Double,
    val result: Double
)
