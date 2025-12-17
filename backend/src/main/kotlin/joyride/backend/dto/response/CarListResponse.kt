package joyride.backend.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CarListItemDto(
    val id: String,
    val brand: String,
    val model: String,
    val buildYear: Int,
    val transmissionType: String,
    val fuelType: String,
    val pricePerDay: Double,
    val coverPhotoUrl: String?
)
