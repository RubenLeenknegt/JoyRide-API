package leafcar.backend.domain.auth

import leafcar.backend.domain.User

/**
 * Represents the credentials of a user in the system.
 *
 * This class is used to store the user's associated account information,
 * including their user details and hashed password.
 *
 * @property user The `User` object containing the details of the user.
 * @property passwordHash The hashed password of the user.
 */
data class UserCredentials(
    val user: User,
    val passwordHash: String
)