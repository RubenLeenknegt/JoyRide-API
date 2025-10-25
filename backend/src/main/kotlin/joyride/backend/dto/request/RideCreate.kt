package joyride.backend.dto.request

import kotlinx.serialization.Serializable

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
