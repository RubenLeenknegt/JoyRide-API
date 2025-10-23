package leafcar.backend.services

import kotlinx.datetime.LocalDate
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Service responsible for handling authentication and user registration.
 *
 * @property userRepository Repository for accessing user data.
 * @property encoder Password encoder for hashing and verifying passwords.
 */
class AuthService(
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {

    /**
     * Verifies if the provided password matches the stored password hash for the given email address.
     *
     * @param emailAddress The email address of the user.
     * @param password The plain text password to verify.
     * @return The `User` object if the password matches, or `null` if it does not.
     */
    fun verifyPassword(emailAddress: String, password: String): User? {
        val creds = userRepository.findCredentialsByEmail(emailAddress) ?: return null
        return if (encoder.matches(password, creds.passwordHash)) creds.user else null
    }

    /**
     * Creates a hashed version of the provided plain text password.
     *
     * @param password The plain text password to hash.
     * @return The hashed password as a `String`.
     */
    fun createPasswordHash(password: String): String {
        return encoder.encode(password)
    }

    /**
     * Registers a new user with the provided details.
     *
     * @param emailAddress The email address of the new user.
     * @param password The plain text password for the new user.
     * @param firstName The first name of the new user.
     * @param lastName The last name of the new user.
     * @param birthDate The birth date of the new user.
     * @param userType The type of user (e.g., admin, customer).
     * @param bankAccount The bank account number of the user (optional).
     * @param bankAccountName The name associated with the bank account (optional).
     * @return The created `User` object if registration is successful, or `null` if the email is already in use.
     */
    fun registration(
        emailAddress: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: LocalDate,
        userType: UserType,
        bankAccount: String?,
        bankAccountName: String?,
    ): User? {
        if (userRepository.findByEmail(emailAddress) == null) {
            val passwordHashed = createPasswordHash(password)
            val created = userRepository.createUser(
                emailAddress = emailAddress,
                passwordHash = passwordHashed,
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDate,
                userType = userType,
                bankAccount = bankAccount,
                bankAccountName = bankAccountName,
            )
            return created
        } else {
            return null
        }
    }
}