package leafcar.backend.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val emailAddress: String,
    val password: String
)