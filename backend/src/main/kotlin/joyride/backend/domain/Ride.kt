package joyride.backend.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Domain model representing a completed or ongoing car ride.
 *
 * This model is persistence-agnostic and represents a ride as it is used within
 * the business and application layers.
 *
 * @property id Unique identifier of the ride.
 * @property startX Starting X-coordinate of the ride.
 * @property startY Starting Y-coordinate of the ride.
 * @property endX Ending X-coordinate of the ride.
 * @property endY Ending Y-coordinate of the ride.
 * @property length Total distance between start and end point in meters.
 * @property duration Duration of the ride in seconds.
 * @property reservationId Identifier of the reservation this ride is linked to.
 * @property dateTimeStart Start date and time of the ride.
 * @property dateTimeEnd End date and time of the ride.
 * @property distanceTravelled Total distance travelled during the ride, expressed in kilometers.
 * @property name Optional human-readable name or label for the ride.
 *
 * A ride always belongs to an existing reservation and captures both spatial and
 * temporal data describing the driven route.
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
    val reservationId: String,
    val dateTimeStart: LocalDateTime,
    val dateTimeEnd: LocalDateTime,
    var distanceTravelled: Double,
    var name: String? = null
)