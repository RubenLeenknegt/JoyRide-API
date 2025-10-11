package leafcar.backend.api.auth

import kotlinx.serialization.Serializable
import leafcar.backend.domain.User

@Serializable
data class LoginResponse(
    val user: User,
    val message: String = "Login successful"
)