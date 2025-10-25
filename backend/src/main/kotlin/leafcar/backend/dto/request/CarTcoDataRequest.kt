package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * DTO representing the Total Cost of Ownership (TCO) data of a car.
 * Used for API requests/responses to provide cost-related information
 * for evaluating car ownership expenses.
 */

@Serializable
data class CarTcoDataRequest(
    val id: String,
    val purchasePrice: Double,
    val residualValue: Double,
    val usageYears: Int,
    val annualKm: Int,
    val energyCostPerKm: Double,
    val maintenanceCostPerKm: Double
)
