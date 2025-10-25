// kotlin
package joyride.backend.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import joyride.backend.EnvironmentSetup
import joyride.backend.services.JwtConfig.dotenv
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.Date
import kotlin.test.assertFalse

class JwtConfigTest {
    @BeforeEach
    fun setUp() {
        EnvironmentSetup.setup()
    }

    private val jwt = JwtConfig
    private val audience = "test-API-Audience"
    private val userId = "a2d329aa-43f7-4c67-aa30-3815ecab028e"

    @Test
    fun generateAccessToken_valid() {
        val token = jwt.generateAccessToken(userId, audience)
        assertTrue { jwt.verifyToken(token, audience, "access") }
    }

    @Test
    fun generateRefreshToken_valid() {
        val token = jwt.generateRefreshToken(userId, audience)
        assertTrue { jwt.verifyToken(token, audience, "refreshToken") }
    }

    @Test
    fun verifyToken_invalidRandomString_isFalse() {
        val wrong = "dit is fout"
        assertFalse { jwt.verifyToken(wrong, audience, "access") }
    }

    @Test
    fun verifyToken_missingIdClaim_isFalse() {
        val tokenNoUserId =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJsZWFmY2FyLWFwaSIsImlzcyI6ImxlYWZjYXIuY29tIiwidG9rZW5UeXBlIjoiYWNjZXNzIiwiZXhwIjoxNzYxMDQ5ODE2fQ.o-ds21ndLOpcb_7GGso0S2WDcC-rUzfpzTZ3YajDTgRg"
        assertFalse { jwt.verifyToken(tokenNoUserId, audience, "access") }
    }

    @Test
    fun verifyToken_emptyId_isFalse() {
        val secretTest = dotenv["JWT_SECRET"] // Secret key for signing tokens
        val issuerTest = dotenv["JWT_ISSUER"] // Token issuer
        val aTValidityInMsTest = 15 * 60 * 1000 // Access token validity (15 minutes)
        val tokenEmptyId = JWT.create()
            .withAudience(audience)
            .withIssuer(issuerTest)
            .withClaim("id", "")
            .withClaim("tokenType", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + aTValidityInMsTest)) // 15 min
            .sign(Algorithm.HMAC256(secretTest))
        assertFalse { jwt.verifyToken(tokenEmptyId, audience, "access") }
    }

    @Test
    fun verifyToken_nullId_isFalse() {
        val secretTest = dotenv["JWT_SECRET"] // Secret key for signing tokens
        val issuerTest = dotenv["JWT_ISSUER"] // Token issuer
        val aTValidityInMsTest = 15 * 60 * 1000 // Access token validity (15 minutes)
        val tokenNullId = JWT.create()
            .withAudience(audience)
            .withIssuer(issuerTest)
            .withClaim("id", "")
            .withClaim("tokenType", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + aTValidityInMsTest)) // 15 min
            .sign(Algorithm.HMAC256(secretTest))
        assertFalse { jwt.verifyToken(tokenNullId, audience, "access") }
    }

    @Test
    fun verifyToken_refreshToken_isNotValidAsAccess() {
        val refresh = jwt.generateRefreshToken(userId, audience)
        assertFalse { jwt.verifyToken(refresh, audience, "access") }
    }
}
