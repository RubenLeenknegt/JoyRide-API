package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * DTO representing the location of a car.
 * Used for API requests/responses when only the car's coordinates are needed.
 */

@Serializable
data class CarLocationRequest(
    val id: String,
    val locationX: Float,
    val locationY: Float
)
