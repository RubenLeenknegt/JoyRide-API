package leafcar.backend.dto.response

import kotlinx.serialization.Serializable
import leafcar.backend.dto.UserDto

@Serializable
data class LoginResponse(
    val user: UserDto,
    val message: String = "Login successful",
    val token: String
)