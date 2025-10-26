package joyride.backend.domain

import kotlinx.serialization.Serializable

/**
 * Domain model representing a completed or ongoing car ride.
 *
 * @property id Unique identifier of the ride.
 * @property startX Starting X-coordinate of the ride.
 * @property startY Starting Y-coordinate of the ride.
 * @property endX Ending X-coordinate of the ride.
 * @property endY Ending Y-coordinate of the ride.
 * @property length Total ride distance in meters.
 * @property duration Duration of the ride in seconds.
 * @property reservationId Identifier of the reservation this ride is linked to.
 *
 * A ride always belongs to an existing reservation and captures spatial and temporal data
 * describing the driven route.
 */

@Serializable
data class Ride(
    val id: String,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val length: Int,
    val duration: Int,
    val reservationId: String
)