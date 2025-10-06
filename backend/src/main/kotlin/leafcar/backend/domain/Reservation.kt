package leafcar.backend.domain

import kotlinx.serialization.Serializable

@Serializable
data class Reservation(
    val id: String,
    val userId: String,
    val carId: String,
    val startDate: String,
    val endDate: String
) {
    init {
        require(endDate > startDate) { "Reservation end date must be after start date" }
    }
}