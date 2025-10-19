package leafcar.backend.dto.response

import kotlinx.serialization.Serializable

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
