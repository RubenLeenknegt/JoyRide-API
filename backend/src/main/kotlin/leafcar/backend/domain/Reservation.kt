package leafcar.backend.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Reservation(
    val id: String,
    val userId: String,
    val carId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
) {
    init {
        require(endDate > startDate) { "Reservation end date must be after start date" }
    }
}