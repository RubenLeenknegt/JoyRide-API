package leafcar.backend.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.github.cdimascio.dotenv.dotenv
import java.util.Date

/**
 * Object responsible for handling JWT (JSON Web Token) generation and verification.
 *
 * This configuration uses HMAC256 algorithm for signing tokens and retrieves
 * secret and issuer information from environment variables.
 */
object JwtConfig {
    val dotenv = dotenv()
    private val secret = dotenv["JWT_SECRET"] // Secret key for signing tokens
    private val issuer = dotenv["JWT_ISSUER"] // Token issuer
    private const val ATVALIDITYINMS = 15 * 60 * 1000 // Access token validity (15 minutes)
    private const val RTVALIDITYINMS = 7 * 24 * 60 * 60 * 1000 // Refresh token validity (7 days)

    private val algorithm = Algorithm.HMAC256(secret) // Algorithm used for signing tokens

    /**
     * Generates an access token with a short expiration time.
     *
     * @param id The unique identifier of the user.
     * @param audience The audience for which the token is intended.
     * @return A signed JWT access token as a `String`.
     */
    fun generateAccessToken(id: String, audience: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", id)
            .withClaim("tokenType", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + ATVALIDITYINMS)) // 15 min
            .sign(Algorithm.HMAC256(secret))
    }

    /**
     * Generates a refresh token with a longer expiration time.
     *
     * @param id The unique identifier of the user.
     * @param audience The audience for which the token is intended.
     * @return A signed JWT refresh token as a `String`.
     */
    fun generateRefreshToken(id: String, audience: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", id)
            .withClaim("tokenType", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + RTVALIDITYINMS)) // 7 days
            .sign(Algorithm.HMAC256(secret))
    }

    /**
     * Verifies the validity of a given token.
     *
     * @param token The JWT token to verify.
     * @param audience The expected audience of the token.
     * @param expectedToken The expected token type (e.g., "access" or "refresh").
     * @return `true` if the token is valid and matches the expected type, `false` otherwise.
     */
    fun verifyToken(token: String, audience: String, expectedToken: String): Boolean {
        return try {
            val verifier: JWTVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
            val verified = verifier.verify(token)
            return (verified.getClaim("tokenType").asString() == expectedToken) && (!verified.getClaim("id").asString()
                .isNullOrBlank())
        } catch (ex: JWTVerificationException) {
            false
        }
    }
}