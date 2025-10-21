package leafcar.backend.repository

import kotlinx.datetime.LocalDate
import leafcar.backend.dao.UsersTable
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.dao.UserEntity
import leafcar.backend.domain.auth.UserCredentials
import leafcar.backend.mappers.UserMapper.toDomain
import leafcar.backend.mappers.UserMapper.toUserType
import leafcar.backend.services.AuthService
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * Repository class for managing user data.
 *
 * This class provides methods to perform CRUD operations on user data
 * using the Exposed ORM framework.
 */
class UserRepository {

    /**
     * Retrieves all users from the database.
     *
     * @return A list of `User` domain objects.
     */
    fun getAll(): List<User> = transaction {
        UserEntity.all().map { it.toDomain() }
    }

    /**
     * Creates a new user in the database.
     *
     * @param emailAddress The email address of the user.
     * @param passwordHash The hashed password of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param birthDate The birth date of the user.
     * @param userType The type of the user (e.g., admin, customer).
     * @param bankAccount The bank account number of the user (optional).
     * @param bankAccountName The name associated with the bank account (optional).
     * @param vehicleLocation The location of the user's vehicle (optional).
     * @return The created `User` domain object.
     */
    fun createUser(
        emailAddress: String,
        passwordHash: String,
        firstName: String,
        lastName: String,
        birthDate: LocalDate,
        userType: UserType,
        bankAccount: String? = null,
        bankAccountName: String? = null,
        vehicleLocation: String? = null
    ): User = transaction {
        val user = UserEntity.new(UUID.randomUUID().toString()) {
            this.firstName = firstName
            this.lastName = lastName
            this.birthDate = birthDate
            this.emailAddress = emailAddress
            this.userType = userType
            this.passwordHash = passwordHash
            this.bankAccount = bankAccount
            this.bankAccountName = bankAccountName
            this.vehicleLocation = vehicleLocation
        }
        user.toDomain()
    }

    /**
     * Finds a user by their email address.
     *
     * @param emailAddress The email address to search for.
     * @return The `User` domain object if found, or `null` if not found.
     */
    fun findByEmail(emailAddress: String) = transaction {
        UserEntity.find { UsersTable.emailAddress eq emailAddress }.firstOrNull()?.toDomain()
    }

    /**
     * Retrieves user credentials (user and password hash) by email address.
     *
     * @param emailAddress The email address to search for.
     * @return A `UserCredentials` object containing the user and their password hash, or `null` if not found.
     */
    fun findCredentialsByEmail(emailAddress: String): UserCredentials? = transaction {
        UserEntity.find { UsersTable.emailAddress eq emailAddress }
            .firstOrNull()?.let { entity -> UserCredentials(entity.toDomain(), entity.passwordHash) }
    }

    /**
     * Updates a specific attribute of a user.
     *
     * @param key The attribute to update (e.g., "firstName", "emailAddress").
     * @param value The new value for the attribute.
     * @param id The ID of the user to update.
     * @return A `UserUpdateResult` indicating success or error.
     */
    fun updateVariables(key: String, value: String, id: String): UserUpdateResult = transaction {
        val allowedVariables = listOf(
            "firstName", "lastName", "emailAddress", "password", "userType", "bankAccount", "bankAccountName",
            "vehicleLocation"
        )

        if (key !in allowedVariables) {
            return@transaction UserUpdateResult.Error("Not allowed to edit attribute")
        }

        val userEntity = UserEntity.findById(id) ?: return@transaction UserUpdateResult.Error("The user was not found")
        when (key) {
            "firstName" -> userEntity.firstName = value
            "lastName" -> userEntity.lastName = value
            "emailAddress" -> userEntity.emailAddress = value
            "password" -> userEntity.passwordHash = AuthService(this@UserRepository).createPasswordHash(value)
            "userType" -> userEntity.userType = toUserType(value)
            "bankAccount" -> userEntity.bankAccount = value
            "bankAccountName" -> userEntity.bankAccountName = value
            "vehicleLocation" -> userEntity.vehicleLocation = value
            else -> return@transaction UserUpdateResult.Error("Unknown key")
        }

        UserUpdateResult.Success(userEntity.toDomain())
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id The ID of the user to delete.
     */
    fun deleteUser(id: String) {
        val user = transaction {
            UserEntity.findById(id)
        }
        transaction {
            user?.delete()
        }
    }
}