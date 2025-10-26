package joyride.backend.dto.request

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

/**
 * Request DTO to create or update a car availability period.
 *
 * @property carId Identifier of the car for which the availability is defined.
 * @property startDate Start of the availability period (local date-time).
 * @property endDate Optional end of the availability period; `null` indicates the
 * availability continues indefinitely from [startDate].
 */

@Serializable
data class AvailibilityCreateOrUpdate(
    val carId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null
)
