package leafcar.backend.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.github.cdimascio.dotenv.dotenv
import java.util.Date


object JwtConfig {
    val dotenv = dotenv()
    private val secret = dotenv["JWT_SECRET"]
    private val issuer = dotenv["JWT_ISSUER"]
    private const val ATVALIDITYINMS = 15 * 60 * 1000 // 15 minutes
    private const val RTVALIDITYINMS = 7 * 24 * 60 * 60 * 1000 // 7 days

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateAccessToken(id: String, audience: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", id)
            .withClaim("tokenType", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + ATVALIDITYINMS)) // 15 min
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(id: String, audience: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", id)
            .withClaim("tokenType", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + RTVALIDITYINMS)) // 7 days
            .sign(Algorithm.HMAC256(secret))
    }

    fun verifyToken(token: String, audience: String, expectedToken: String): Boolean {
        return try {
            val verifier: JWTVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
            val verified = verifier.verify(token)
            return (verified.getClaim("tokenType").asString() == expectedToken) && (!verified.getClaim("id").asString().isNullOrBlank())
        } catch (ex: JWTVerificationException) {
            false
        }
    }

}