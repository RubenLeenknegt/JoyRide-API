package leafcar.backend.controller


import com.auth0.jwt.JWT
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
import leafcar.backend.dto.response.LoginResponse
import leafcar.backend.repository.UserRepository
import leafcar.backend.services.Auth
import leafcar.backend.dto.request.RegisterRequest
import leafcar.backend.mappers.UserMapper.toDto
import leafcar.backend.services.JwtConfig

fun Route.authRouting(userRepository: UserRepository) {
    val auth = Auth(userRepository)
    val dotenv = dotenv()
    val audience = dotenv["JWT_AUDIENCE"]
//    Check if expected variables are actually present in the tokens claims
//    change emailaddress to userId

    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>()
            val id = principal!!.payload.getClaim("id").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            if (expiresAt != null) {
                if (expiresAt < 700000) {
                    val token = JwtConfig.generateAccessToken(id, audience)
                    call.respondText(
                        "Hello, $id! Token is expired at $expiresAt ms. Here is your new token ${
                            hashMapOf(
                                "token" to token
                            )
                        }"
                    )

                } else if (expiresAt > 700000) {
                    call.respondText("Hello, $id! Token is expired at $expiresAt ms.")
                }
            }
            call.respond(HttpStatusCode.Unauthorized, "token is expired or does not allow access to this source")

        }
    }
    post("/auth/users/login") {
        val request = call.receive<LoginRequest>()
        val user = auth.verifyPassword(emailAddress = request.emailAddress, password = request.password)
        if (user != null) {
            val accessToken: String = JwtConfig.generateAccessToken(user.id, audience)
            val refreshToken: String = JwtConfig.generateRefreshToken(user.id, audience)

            call.response.cookies.append("refreshToken", refreshToken, httpOnly = true, path = "/")
            call.respond(
                HttpStatusCode.OK, LoginResponse(user = user.toDto(), token = accessToken)
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
            userType = request.user.userType,
            bankAccount = request.user.bankAccount,
            bankAccountName = request.user.bankAccountName,
            vehicleLocation = request.user.vehicleLocation
        )
        if (created != null) {
            val accessToken = JwtConfig.generateAccessToken(created.id, audience)
            val refreshToken = JwtConfig.generateRefreshToken(created.id, audience)
            call.response.cookies.append("refreshToken", refreshToken, httpOnly = true, path = "/")
            call.respond(HttpStatusCode.Created, LoginResponse(created.toDto(), token = accessToken))
        } else {
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already registered"))
        }
    }

    post("/refresh") {
        val expectedToken = "refreshToken"
        val refreshToken = call.request.cookies["refreshToken"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "No refresh token provided")

        val verifier = JwtConfig.verifyToken(refreshToken, audience, expectedToken = expectedToken)
        if (!verifier) {
            return@post call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
        }

        val id = JWT.decode(refreshToken).getClaim("id").asString()

        // Optional: check in DB if this refresh token is valid
        // if (!refreshTokenRepository.isValid(refreshToken)) { ... }

        // Generate new tokens
        val newAccessToken = JwtConfig.generateAccessToken(id, audience)
        val newRefreshToken = JwtConfig.generateRefreshToken(id, audience)

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
