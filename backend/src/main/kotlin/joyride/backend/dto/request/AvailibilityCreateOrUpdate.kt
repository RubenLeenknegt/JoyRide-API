package joyride.backend.dto.request

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class AvailibilityCreateOrUpdate(
    val carId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null
)
