package joyride.backend.domain

import kotlinx.serialization.Serializable

/**
 * Represents the BonusPoints domain model.
 *
 * This data class is used to encapsulate the bonus points information
 * associated with a user and a ride in the system.
 *
 * @property id The unique identifier for the bonus points entry (UUID as a string).
 * @property userId The unique identifier of the user associated with the bonus points.
 * @property rideId The unique identifier of the ride associated with the bonus points.
 * @property points The number of bonus points awarded to the user for the ride.
 */
@Serializable
data class BonusPoints(
    val id: String,
    val userId: String,
    val rideId: String,
    val points: Int
)