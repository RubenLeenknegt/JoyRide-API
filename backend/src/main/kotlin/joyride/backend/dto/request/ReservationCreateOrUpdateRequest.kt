package joyride.backend.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReservationCreateOrUpdateRequest (
    val userId: String,
    val carId: String,
    val startDate: String,
    val endDate: String
)