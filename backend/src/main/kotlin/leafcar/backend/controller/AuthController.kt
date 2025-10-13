package leafcar.backend.controller


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import leafcar.backend.api.auth.LoginRequest
import leafcar.leafcar.backend.dto.response.LoginResponse
import leafcar.backend.repository.UserRepository
import leafcar.backend.service.Auth
import leafcar.leafcar.backend.dto.request.RegisterRequest
import leafcar.leafcar.backend.service.JwtConfig

fun Route.authRouting(userRepository: UserRepository) {
    val auth = Auth(userRepository)
    val dotenv = dotenv()
    val secret = dotenv["JWT_SECRET"]
    val issuer = dotenv["JWT_ISSUER"]
    val audience = dotenv["JWT_AUDIENCE"]
    val jwtRealm = dotenv["JWT_REALM"]


    authenticate("auth-jwt") {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("emailAddress").asString()
            val expiresAt = principal!!.expiresAt?.time?.minus(System.currentTimeMillis())
            if (expiresAt != null) {
                if (expiresAt < 700000) {
                    val token = JwtConfig.generateAccessToken(username, audience)
                    call.respondText(
                        "Hello, $username! Token is expired at $expiresAt ms. Here is your new token ${
                            hashMapOf(
                                "token" to token
                            )
                        }"
                    )

                } else if (expiresAt > 700000) {
                    call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
                }
            }
            call.respond(HttpStatusCode.Unauthorized, "token is expired or does not allow access to this source")

        }
    }
    post("/auth/users/login") {
        val request = call.receive<LoginRequest>()
        val user = auth.verifyPassword(emailAddress = request.emailAddress, password = request.password)
        if (user != null) {
            val accessToken: String = JwtConfig.generateAccessToken(request.emailAddress, audience)
            val refreshToken: String = JwtConfig.generateRefreshToken(request.emailAddress, audience)

            call.response.cookies.append("refreshToken", refreshToken, httpOnly = true, path = "/")
            call.respond(HttpStatusCode.OK, LoginResponse(user = user, token = accessToken)
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
    }

    post("/auth/users/register") {
        val request = call.receive<RegisterRequest>()
        val created = auth.registration(
            emailAddress = request.user.emailAddress,
            password = request.password,
            firstName = request.user.firstName,
            lastName = request.user.lastName,
            birthDate = request.user.birthDate,
            userType = request.user.userType
        )
        if (created != null) {
            val accessToken = JwtConfig.generateAccessToken(request.user.emailAddress, audience)
            val refreshToken = JwtConfig.generateRefreshToken(request.user.emailAddress, audience)
            call.response.cookies.append("refreshToken", refreshToken, httpOnly = true, path = "/")
            call.respond(HttpStatusCode.Created, LoginResponse(created, token = accessToken))
        } else {
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already registered"))
        }
    }

    post("/refresh") {
        val refreshToken = call.request.cookies["refreshToken"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "No refresh token provided")

        val verifier = JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(issuer)
            .withAudience(audience)
            .build()

        val decoded = try {
            verifier.verify(refreshToken)
        } catch (e: Exception) {
            return@post call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
        }

        val emailAddress = decoded.getClaim("emailAddress").asString()

        // Optional: check in DB if this refresh token is valid
        // if (!refreshTokenRepository.isValid(refreshToken)) { ... }

        // Generate new tokens
        val newAccessToken = JwtConfig.generateAccessToken(emailAddress, audience)
        val newRefreshToken = JwtConfig.generateRefreshToken(emailAddress, audience)

        // Optional: store new refresh token and invalidate the old one
        // refreshTokenRepository.store(newRefreshToken, userId)

        call.response.cookies.append(
            "refreshToken",
            newRefreshToken,
            httpOnly = true,
            path = "/"
        )

        call.respond(mapOf("accessToken" to newAccessToken))
    }


}
