package leafcar.leafcar.backend.api.auth

import leafcar.backend.domain.User

data class RegisterRequest(
    val user: User,
    val password: String
)
