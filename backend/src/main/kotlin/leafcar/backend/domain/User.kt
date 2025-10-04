package leafcar.backend.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
     * Represents a user in the system.
     *
     * Marked with `@Serializable` to enable (de)serialization using Kotlinx Serialization.
     *
     * @property id The unique identifier of the user.
     * @property firstName The first name of the user.
     * @property lastName The last name of the user.
     * @property birthDate The birth date of the user, represented as a `LocalDate`.
     * @property emailAddress The email address of the user.
     * @property passwordHash The hashed password of the user for authentication purposes.
     * @property userType The role of the user in the system, represented by the `UserType` enum.
     */
    @Serializable
    data class User(
        val id: String,
        val firstName: String,
        val lastName: String,
        val birthDate: LocalDate,
        val emailAddress: String,
        val passwordHash: String,
        val userType: UserType,
    ) {

    }
