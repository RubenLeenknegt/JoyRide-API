package leafcar.backend.domain

import kotlinx.serialization.Serializable

@Serializable
data class Availability(
    val id: String,
    val carId: String,
    val availableFrom: String,
    val availableTo: String?
) {

}