package leafcar.backend.service

import leafcar.backend.domain.User
import leafcar.backend.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class Authentication(
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {
    fun verifyPassword(email: String, password: String): User? {
        val creds = userRepository.findCredentialsByEmail(email) ?: return null
        return if (encoder.matches(password, creds.passwordHash)) creds.user else null
    }
    fun createPasswordHash(password: String): String {
        return encoder.encode(password)
    }
}