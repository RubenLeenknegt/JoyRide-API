package joyride.backend.dto.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RideListItemResponse(
    val id: String,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val length: Int,
    val duration: Int,
    val reservationId: String,
    val reservationStart: LocalDateTime,
    val reservationEnd: LocalDateTime,
    val carBrand: String,
    val carModel: String,
    val photoCoverPhotoUrl: String
)