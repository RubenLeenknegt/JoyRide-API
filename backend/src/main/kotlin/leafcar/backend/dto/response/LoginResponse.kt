package leafcar.leafcar.backend.dto.response

import kotlinx.serialization.Serializable
import leafcar.backend.domain.User

@Serializable
data class LoginResponse(
    val user: User,
    val message: String = "Login successful",
    val token: HashMap<String, String>? = null
)