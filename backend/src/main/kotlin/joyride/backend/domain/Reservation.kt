package joyride.backend.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Domain model representing a car reservation made by a user.
 *
 * @property id Unique identifier of the reservation.
 * @property userId Identifier of the user who made the reservation.
 * @property carId Identifier of the reserved car.
 * @property startDate Start date and time of the reservation.
 * @property endDate End date and time of the reservation; must be later than [startDate].
 *
 * Ensures valid time range by enforcing that [endDate] occurs after [startDate].
 */

@Serializable
data class Reservation(
    val id: String,
    val userId: String,
    val carId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: ReservationStatus
) {
    init {
        require(endDate > startDate) { "Reservation end date must be after start date" }
    }
}