package leafcar.backend.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CarCpkDataResponse(
    val id: String,
    val ownerId: String,
    val averageConsumption: Double,
    val fuelType: String,
    val fuelPrice: Double,
    val cpk: Double
)
