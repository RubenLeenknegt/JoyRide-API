package leafcar.backend.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import leafcar.backend.domain.UserType

/**
 * Data Transfer Object (DTO) for representing user information.
 *
 * This class is used to transfer user data between different layers of the application.
 * It includes fields for user identification, personal details, and optional financial and vehicle-related information.
 *
 * @property id The unique identifier of the user (optional).
 * @property firstName The first name of the user.
 * @property lastName The last name of the user.
 * @property birthDate The birth date of the user.
 * @property emailAddress The email address of the user.
 * @property userType The type of the user (e.g., admin, customer).
 * @property bankAccount The bank account number of the user (optional).
 * @property bankAccountName The name associated with the bank account (optional).
 */
@Serializable
data class UserDto(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val emailAddress: String,
    val userType: UserType,
    val bankAccount: String? = null,
    val bankAccountName: String? = null,
)