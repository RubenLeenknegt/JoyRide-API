package leafcar.backend.domain

import kotlinx.serialization.Serializable

@Serializable
data class Available(
    val id: String,
    val carId: String,
    val availableFrom: String,
    val availableTo: String?
) {

}