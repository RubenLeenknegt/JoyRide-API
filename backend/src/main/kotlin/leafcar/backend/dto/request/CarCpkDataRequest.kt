package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CarCpkDataRequest(
    val id: String,
    val ownerId: String,
    val averageConsumption: Double,
    val fuelType: String,
)
