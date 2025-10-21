package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

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
