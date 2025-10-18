package leafcar.backend.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CarLocationRequest(
    val id: String,
    val locationX: Float,
    val locationY: Float
)
