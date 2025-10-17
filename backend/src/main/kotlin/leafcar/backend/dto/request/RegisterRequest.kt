package leafcar.backend.dto.request

import kotlinx.serialization.Serializable
import leafcar.backend.domain.User

@Serializable
data class RegisterRequest(
    val user: User,
    val password: String
)
