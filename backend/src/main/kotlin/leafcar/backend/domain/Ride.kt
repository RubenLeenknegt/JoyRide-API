package leafcar.backend.domain

import kotlinx.serialization.Serializable

@Serializable
data class Ride(
    val id: String,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val length: Int,
    val duration: Int,
    val reservationId: String
)