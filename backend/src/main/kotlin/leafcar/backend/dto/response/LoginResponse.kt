package leafcar.backend.dto.response

import kotlinx.serialization.Serializable
import leafcar.backend.dto.UserDto

/**
 * Data Transfer Object (DTO) for representing the response of a login operation.
 *
 * This class is used to encapsulate the data returned to the client after a successful login.
 *
 * @property user The `UserDto` object containing the details of the logged-in user.
 * @property message A message indicating the status of the login operation (default: "Login successful").
 * @property accesstoken The authentication token issued to the user for subsequent requests.
 */
@Serializable
data class LoginResponse(
    val user: UserDto,
    val message: String = "Login successful",
    val accessToken: String
)