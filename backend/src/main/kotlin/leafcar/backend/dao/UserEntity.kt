package leafcar.backend.dao

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Represents the User entity in the database.
 *
 * This class maps the `UsersTable` database table to a Kotlin object
 * and provides access to the table's columns via delegated properties.
 *
 * @constructor Creates a new `UserEntity` with the given ID.
 * @property firstName The first name of the user, mapped to the `first_name` column in the `UsersTable`.
 * @property lastName The last name of the user, mapped to the `last_name` column in the `UsersTable`.
 * @property birthDate The birth date of the user, mapped to the `birth_date` column in the `UsersTable`.
 * @property emailAddress The email address of the user, mapped to the `email_address` column in the `UsersTable`.
 * @property userType The role of the user in the system, mapped to the `user_type` column in the `UsersTable`.
 * @property passwordHash The hashed password of the user, mapped to the `password_hash` column in the `UsersTable`.
 * @property bankAccount The bank account number of the user, mapped to the `bank_account` column in the `UsersTable`.
 * @property bankAccountName The name associated with the user's bank account, mapped to the `bank_account_name` column in the `UsersTable`.
 */
class UserEntity(id: EntityID<String>) : Entity<String>(id) {
    /**
     * Companion object that acts as a DAO (Data Access Object) for the `UserEntity`.
     * Provides methods for querying and interacting with the `UsersTable`.
     */
    companion object : EntityClass<String, UserEntity>(UsersTable)

    // Maps to the `first_name` column in the `UsersTable`.
    var firstName by UsersTable.firstName

    // Maps to the `last_name` column in the `UsersTable`.
    var lastName by UsersTable.lastName

    // Maps to the `birth_date` column in the `UsersTable`.
    var birthDate by UsersTable.birthDate

    // Maps to the `email_address` column in the `UsersTable`.
    var emailAddress by UsersTable.emailAddress

    // Maps to the `user_type` column in the `UsersTable`.
    var userType by UsersTable.userType

    // Maps to the `password_hash` column in the `UsersTable`.
    // Stores the hashed password for the user.
    var passwordHash by UsersTable.passwordHash

    // Maps to the `bank_account` column in the `UsersTable`.
    var bankAccount by UsersTable.bankAccount

    // Maps to the `bank_account_name` column in the `UsersTable`.
    var bankAccountName by UsersTable.bankAccountName

}