package joyride.backend.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Domain model representing an availability time window for a car.
 *
 * @property id Unique identifier of the availability entry.
 * @property carId Identifier of the car this availability belongs to.
 * @property startDate Start date-time of the availability (`kotlinx.datetime.LocalDateTime`).
 * @property endDate Optional end date-time of the availability; when non\-null it must be after [startDate].
 */

@Serializable
data class Availability(
    val id: String,
    val carId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?
) {
    init {
        if (endDate != null) {
            require(endDate > startDate) { "Availability end date must be after start date" }
        }
    }
}
