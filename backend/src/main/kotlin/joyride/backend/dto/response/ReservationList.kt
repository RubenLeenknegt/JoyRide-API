package joyride.backend.dto.response

import joyride.backend.domain.ReservationStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Data transfer object representing a reservation with enriched car information.
 *
 * This DTO combines reservation details with essential car information (brand, model, cover photo)
 * to support efficient display of reservation lists in the UI. It eliminates the need for multiple
 * API calls by providing all necessary information in a single response.
 *
 * @property id Unique identifier of the reservation.
 * @property userId Identifier of the user who made the reservation.
 * @property carId Identifier of the reserved car.
 * @property startDate Start date and time of the reservation.
 * @property endDate End date and time of the reservation.
 * @property carBrand Brand name of the reserved car (e.g., "BMW", "Tesla").
 * @property carModel Model name of the reserved car (e.g., "3 Series", "Model S").
 * @property coverPhotoUrl Full URL to the car's cover photo. Empty string if no photo is available.
 */
@Serializable
data class ReservationList(
    val id: String,
    val userId: String,
    val carId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: ReservationStatus,
    val carBrand: String,
    val carModel: String,
    val coverPhotoUrl: String?
)