package leafcar.backend.service

import kotlinx.datetime.LocalDate
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class Auth(
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {
    fun verifyPassword(emailAddress: String, password: String): User? {
        val creds = userRepository.findCredentialsByEmail(emailAddress) ?: return null
        return if (encoder.matches(password, creds.passwordHash)) creds.user else null
    }

    fun createPasswordHash(password: String): String {
        return encoder.encode(password)
    }

    fun registration(
        emailAddress: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: LocalDate,
        userType: UserType
    ): User? {
        if (userRepository.findByEmail(emailAddress) == null) {
            val passwordHashed = createPasswordHash(password)
            val created = userRepository.createUser(
                emailAddress = emailAddress,
                passwordHash = passwordHashed,
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDate,
                userType = userType
            )
            return created
        } else {
            return null
        }
    }
}