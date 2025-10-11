package leafcar.backend.repository

import kotlinx.datetime.LocalDate
import leafcar.backend.dao.UsersTable
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.dao.UserEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.auth.UserCredentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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
    ): User? = transaction {
        val user = UserEntity.new(UUID.randomUUID().toString()) {
            this.firstName = firstName
            this.lastName = lastName
            this.birthDate = birthDate
            this.emailAddress = emailAddress
            this.userType = userType
            this.passwordHash = passwordHash
        }
        user.toDomain()
    }

    fun findByEmail(email: String) = transaction {
        UserEntity.find { UsersTable.emailAddress eq email }.firstOrNull()?.toDomain()
    }

    fun findCredentialsByEmail(email: String): UserCredentials? = transaction {
        UserEntity.find { UsersTable.emailAddress eq email }
            .firstOrNull()
            ?.let { entity -> UserCredentials(entity.toDomain(), entity.passwordHash) }
    }
}