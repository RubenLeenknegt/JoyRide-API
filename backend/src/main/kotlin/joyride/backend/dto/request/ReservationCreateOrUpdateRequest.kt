package joyride.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * Request DTO to create or update a reservation.
 *
 * @property userId Identifier of the user making the reservation.
 * @property carId Identifier of the car being reserved.
 * @property startDate Reservation start date-time as an ISO-8601 local date-time string.
 * @property endDate Reservation end date-time as an ISO-8601 local date-time string.
 */

@Serializable
data class ReservationCreateOrUpdateRequest (
    val userId: String,
    val carId: String,
    val startDate: String,
    val endDate: String
)