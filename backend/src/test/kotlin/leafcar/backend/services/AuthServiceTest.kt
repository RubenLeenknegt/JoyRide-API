import leafcar.backend.repository.UserRepository
import leafcar.backend.services.AuthService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthServiceTest {

    private val userRepository = mock(UserRepository::class.java)
    private val encoder = mock(BCryptPasswordEncoder::class.java)
    private val authService = AuthService(userRepository, encoder)

    @Test
    fun createPasswordHash_returnsHashedPassword() {
        val plainPassword = "securePassword123"
        val hashedPassword = "hashedPassword123"

        `when`(encoder.encode(plainPassword)).thenReturn(hashedPassword)

        val result = authService.createPasswordHash(plainPassword)

        assertEquals(hashedPassword, result)
        verify(encoder).encode(plainPassword)
    }

    @Test
    fun createPasswordHash_handlesEmptyPassword() {
        val plainPassword = ""
        val hashedPassword = "hashedEmptyPassword"

        `when`(encoder.encode(plainPassword)).thenReturn(hashedPassword)

        val result = authService.createPasswordHash(plainPassword)

        assertEquals(hashedPassword, result)
        verify(encoder).encode(plainPassword)
    }
}
