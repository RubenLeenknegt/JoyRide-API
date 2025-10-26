package joyride.backend.dto.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import joyride.backend.domain.UserType

/**
 * Data Transfer Object (DTO) for registering a new user.
 *
 * This class is used to encapsulate the data required to register a new user in the system.
 *
 * @property firstName The first name of the user.
 * @property lastName The last name of the user.
 * @property birthDate The birth date of the user.
 * @property emailAddress The email address of the user.
 * @property userType The type of the user (e.g., admin, customer).
 * @property bankAccount The bank account number of the user (optional).
 * @property bankAccountName The name associated with the bank account (optional).
 * @property password The password chosen by the user for their account.
 */
@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val emailAddress: String,
    val userType: UserType,
    val bankAccount: String? = null,
    val bankAccountName: String? = null,
    val password: String
)