package leafcar.backend.domain

import kotlinx.serialization.Serializable

/**
 * Represents the BonusPoints domain model.
 * This data class is used to encapsulate the bonus points information
 * associated with a user and a ride.
 *
 * @property id The unique identifier for the bonus points entry (UUID as a string).
 * @property userId The ID of the user associated with the bonus points.
 * @property rideId The ID of the ride associated with the bonus points.
 * @property points The number of bonus points awarded.
 */
@Serializable
data class BonusPoints(
    val id: String,
    val userId: String,
    val rideId: String,
    val points: Int
)