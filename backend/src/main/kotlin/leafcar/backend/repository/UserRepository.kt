package leafcar.backend.repository

import kotlinx.datetime.LocalDate
import leafcar.backend.dao.UsersTable
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import org.example.leafcar.backend.dao.UserEntity
import org.example.leafcar.backend.dao.toDomain
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.UUID

class UserRepository {
    fun getAll(): List<User> = transaction {
        UserEntity.all().map { it.toDomain() }
    }

    fun createUser(
        emailAdress: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: LocalDate,
        userType: UserType,
    ): User? = transaction {
        val encoder = BCryptPasswordEncoder()
        val passwordHash: String = encoder.encode(password).toString()
        val user = UserEntity.new(UUID.randomUUID().toString()) {
            this.firstName = firstName
            this.lastName = lastName
            this.birthDate = birthDate
            this.emailAdress = emailAdress
            this.userType = userType
            this.passwordHash = passwordHash
        }
        user.toDomain()
    }

    private fun findByEmail(email: String): UserEntity? =
        UserEntity.find { UsersTable.emailAdress eq email }.firstOrNull()


    fun verifyPassword(email: String, password: String): User? = transaction {
        val userEntity = findByEmail(email) ?: return@transaction null
        val encoder = BCryptPasswordEncoder()

        if (encoder.matches(password, userEntity.passwordHash)) {
            userEntity.toDomain()
        } else {
            null
        }
    }
}