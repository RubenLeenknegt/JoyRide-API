package joyride.backend.domain

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: String,
    val carId: String? = null,
    val reservationId: String? = null,
    val userId: String? = null,
    val filePath: String
)
