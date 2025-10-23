package leafcar.backend.controller

import com.auth0.jwt.JWT
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import leafcar.backend.api.auth.LoginRequest
import leafcar.backend.dto.request.RegisterRequest
import leafcar.backend.dto.response.LoginResponse
import leafcar.backend.mappers.UserMapper.toDto
import leafcar.backend.repository.UserRepository
import leafcar.backend.services.AuthService
import leafcar.backend.services.JwtConfig

/**
 * Defines the authentication-related routes for the application.
 *
 * @param userRepository The repository used to interact with user data.
 */
fun Route.authRouting(userRepository: UserRepository) {
    val authService = AuthService(userRepository)
    val dotenv = dotenv()
    val audience = dotenv["JWT_AUDIENCE"]

    /**
     * Route to handle user login.
     * Verifies the user's credentials and generates access and refresh tokens.
     */
    post("/auth/users/login") {
        val request = call.receive<LoginRequest>()
        val user = authService.verifyPassword(emailAddress = request.emailAddress, password = request.password)
        if (user != null) {
            val accesstoken: String = JwtConfig.generateAccessToken(user.id, audience)
            val refreshtoken: String = JwtConfig.generateRefreshToken(user.id, audience)

            call.response.cookies.append("refreshToken", refreshtoken, httpOnly = true, path = "/")
            call.respond(
                HttpStatusCode.OK, LoginResponse(user = user.toDto(), accessToken = accesstoken)
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
    }

    /**
     * Route to handle user registration.
     * Creates a new user and generates access and refresh tokens.
     */
    post("/auth/users/register") {
        val request = call.receive<RegisterRequest>()
        val created = authService.registration(
            emailAddress = request.emailAddress,
            password = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
            birthDate = request.birthDate,
            userType = request.userType,
            bankAccount = request.bankAccount,
            bankAccountName = request.bankAccountName,
        )
        if (created != null) {
            val accessToken = JwtConfig.generateAccessToken(created.id, audience)
            val refreshToken = JwtConfig.generateRefreshToken(created.id, audience)
            call.response.cookies.append("refreshToken", refreshToken, httpOnly = true, path = "/")
            call.respond(HttpStatusCode.Created, LoginResponse(created.toDto(), accessToken = accessToken))
        } else {
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already registered"))
        }
    }

    /**
     * Route to refresh the user's tokens.
     * Validates the refresh token and generates new access and refresh tokens.
     */
    post("/refresh") {
        val expectedToken = "refreshToken"
        val refreshToken = call.request.cookies["refreshToken"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "No refresh token provided")

        val verifier = JwtConfig.verifyToken(refreshToken, audience, expectedToken = expectedToken)
        if (!verifier) {
            return@post call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
        }

        val id = JWT.decode(refreshToken).getClaim("id").asString()

        // Generate new tokens
        val newAccessToken = JwtConfig.generateAccessToken(id, audience)
        val newRefreshToken = JwtConfig.generateRefreshToken(id, audience)

        call.response.cookies.append(
            "refreshToken",
            newRefreshToken,
            httpOnly = true,
            path = "/"
        )

        call.respond(mapOf("accessToken" to newAccessToken))
    }
}