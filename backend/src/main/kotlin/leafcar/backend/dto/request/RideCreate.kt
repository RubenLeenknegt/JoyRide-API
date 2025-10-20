package leafcar.backend.dto

import kotlinx.serialization.Serializable

@Serializable
data class RideCreateRequest(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val length: Int,
    val duration: Int,
    val reservationId: String
)
