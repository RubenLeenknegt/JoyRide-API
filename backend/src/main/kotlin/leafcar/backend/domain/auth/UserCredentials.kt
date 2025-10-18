package leafcar.backend.domain.auth

import leafcar.backend.domain.User

data class UserCredentials(
    val user: User,
    val passwordHash: String
)