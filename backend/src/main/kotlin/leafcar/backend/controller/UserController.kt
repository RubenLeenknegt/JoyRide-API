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
import java.util.Date

val dotenv = dotenv()
fun Route.userRouting(userRepository: UserRepository) {
    val auth = Auth(userRepository)
    val secret = dotenv["JWT_SECRET"]
    val issuer = dotenv["JWT_ISSUER"]
    val audience = dotenv["JWT_AUDIENCE"]
    val jwtRealm = dotenv["JWT_REALM"]

    route("/users") {
        get {
            val users = userRepository.getAll()
            call.respond(status = HttpStatusCode.OK, users)
        }
    }

    authenticate("auth-jwt") {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("email").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
    post("/users/login") {
        val request = call.receive<LoginRequest>()
        val user = auth.verifyPassword(email = request.email, password = request.password)
        if (user != null) {
            call.respond(HttpStatusCode.OK, LoginResponse(user))
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
    }
    post("/users/register") {
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
            call.respond(HttpStatusCode.Created, LoginResponse(created))
        } else {
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already registered"))
        }
    }
    post("/test") {
        val request = call.receive<LoginRequest>()
        // Check if password matches passwordHash
        val user = auth.verifyPassword(email = request.email, password = request.password)


        if (user != null) {
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("email", request.email)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000000))
                .sign(Algorithm.HMAC256(secret))
            call.respond(HttpStatusCode.OK, LoginResponse(user = user, token = hashMapOf("token" to token)))
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
    }

}
