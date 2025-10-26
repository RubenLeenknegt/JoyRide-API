package joyride.backend.domain

import kotlinx.serialization.Serializable

/**
 * Domain model representing a photo linked to various entities in the application.
 *
 * @property id Unique identifier of the photo.
 * @property carId Optional reference to the car this photo belongs to.
 * @property reservationId Optional reference to the reservation this photo is associated with.
 * @property userId Optional reference to the user who uploaded or is depicted in the photo.
 * @property filePath Absolute or relative path to the stored photo file.
 *
 * A photo may belong to either a car, a reservation, or a user — indicated by one of the optional IDs.
 * Only one of these associations is typically set per instance.
 */


@Serializable
data class Photo(
    val id: String,
    val carId: String? = null,
    val reservationId: String? = null,
    val userId: String? = null,
    val filePath: String
)
