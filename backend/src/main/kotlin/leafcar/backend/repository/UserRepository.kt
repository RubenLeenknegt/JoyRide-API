package leafcar.backend.repository

import kotlinx.datetime.LocalDate
import leafcar.backend.dao.UsersTable
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.dao.UserEntity
import leafcar.backend.domain.auth.UserCredentials
import leafcar.backend.mappers.UserMapper.toDomain
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
            .firstOrNull()
            ?.let { entity -> UserCredentials(entity.toDomain(), entity.passwordHash) }
    }
}