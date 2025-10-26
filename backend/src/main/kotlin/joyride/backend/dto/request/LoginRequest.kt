package joyride.backend.api.auth

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for representing a login request.
 *
 * This class is used to encapsulate the data required for a user to log in.
 *
 * @property emailAddress The email address of the user attempting to log in.
 * @property password The password of the user attempting to log in.
 */
@Serializable
data class LoginRequest(
    val emailAddress: String,
    val password: String
)