package leafcar.backend.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Availability(
    val id: String,
    val carId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?
)
