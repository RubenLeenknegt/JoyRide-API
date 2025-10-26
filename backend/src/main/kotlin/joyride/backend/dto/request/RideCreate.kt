package joyride.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * Request DTO representing a completed or planned ride segment.
 *
 * Contains start/end coordinates, computed metrics and the associated reservation.
 *
 * @property startX X coordinate of the ride start location (float).
 * @property startY Y coordinate of the ride start location (float).
 * @property endX X coordinate of the ride end location (float).
 * @property endY Y coordinate of the ride end location (float).
 * @property length Length of the ride in meters (integer).
 * @property duration Duration of the ride in seconds (integer).
 * @property reservationId Identifier of the reservation associated with this ride.
 */

@Serializable
data class RideCreate(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val length: Int,
    val duration: Int,
    val reservationId: String
)
