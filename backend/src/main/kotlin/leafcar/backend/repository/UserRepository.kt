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

class UserRepository {
    fun getAll(): List<User> = transaction {
        UserEntity.all().map { it.toDomain() }
    }

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

    fun findByEmail(emailAddress: String) = transaction {
        UserEntity.find { UsersTable.emailAddress eq emailAddress }.firstOrNull()?.toDomain()
    }

    fun findCredentialsByEmail(emailAddress: String): UserCredentials? = transaction {
        UserEntity.find { UsersTable.emailAddress eq emailAddress }
            .firstOrNull()?.let { entity -> UserCredentials(entity.toDomain(), entity.passwordHash) }
    }


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

    fun deleteUser(id: String) {
        val user = transaction {
            UserEntity.findById(id)
        }
        transaction {
            user?.delete()
        }
    }

}