package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * DTO representing the Cost Per Kilometer (CPK) data of a car.
 * Used for API requests/responses to provide car efficiency and cost information.
 */

@Serializable
data class CarCpkDataRequest(
    val id: String,
    val ownerId: String,
    val averageConsumption: Double,
    val fuelType: String,
)
