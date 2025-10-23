package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for creating bonus points.
 *
 * This class is used to encapsulate the data required to create bonus points
 * for a specific user and ride.
 *
 * @property userId The unique identifier of the user to whom the bonus points belong.
 * @property rideId The unique identifier of the ride associated with the bonus points.
 * @property points The number of bonus points to be created.
 */
@Serializable
data class BonusPointsCreate(
    val userId: String,
    val rideId: String,
    val points: Int
)