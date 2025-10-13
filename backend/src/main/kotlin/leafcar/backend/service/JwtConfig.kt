package leafcar.leafcar.backend.service

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

    fun generateAccessToken(emailAddress: String, audience: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("emailAddress", emailAddress)
            .withExpiresAt(Date(System.currentTimeMillis() + ATVALIDITYINMS)) // 15 min
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(emailAddress: String, audience: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("emailAddress", emailAddress)
            .withExpiresAt(Date(System.currentTimeMillis() + RTVALIDITYINMS)) // 7 days
            .sign(Algorithm.HMAC256(secret))
    }

    fun verifyToken(token: String, audience: String): Boolean {
        return try {
            val verifier: JWTVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
            verifier.verify(token)
            true
        } catch (ex: JWTVerificationException) {
            false
        }
    }

}