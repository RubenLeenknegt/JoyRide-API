package leafcar.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import java.io.File
import leafcar.backend.controller.*
import leafcar.backend.repository.*
import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.auth.Authentication
import kotlinx.serialization.json.Json
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.auth.jwt.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.auth.parseAuthorizationHeader
import java.util.Date
import leafcar.backend.services.generateUsersMock
import leafcar.backend.services.generateReservationsMock

/**
 * Main application module for the LeafCar backend.
 *
 * Configures:
 * - Database connection using Exposed and HikariCP
 * - JSON serialization with Kotlinx Serialization
 * - JWT authentication for secure endpoints
 * - Repositories for car, user, reservations, bonus points, rides, and photos
 * - Mock data generation for users and reservations
 * - Routing for API endpoints and static content (photos)
 *
 * Serves as the entry point for the Ktor server and initializes
 * all necessary services and routes.
 */
fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // --- Database ---
    // Initialize Exposed with Hikari connection pool
    Database.connect(HikariDataSource(DatabaseConnection.getDataSource()))

    // --- Serialization ---
    // Configure JSON serialization for requests/responses
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true              // leesbare output tijdens ontwikkeling
                isLenient = true                // tolereert licht afwijkende JSON
                ignoreUnknownKeys = true        // negeert extra velden in inkomende JSON

            }
        )
    }

    // --- Serialization ---
    // Configure JSON serialization for requests/responses
    val dotenv = dotenv()
    val secret = dotenv["JWT_SECRET"]
    val issuer = dotenv["JWT_ISSUER"]
    val audience = dotenv["JWT_AUDIENCE"]
    val backendRealm = dotenv["JWT_BACKEND_REALM"]

    install(Authentication) {
        jwt(dotenv["JWT_BACKEND_AUTH_NAME"]) {
            realm = backendRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                val payload = credential.payload
                val id = payload.getClaim("id").asString()
                val tokenType = payload.getClaim("tokenType").asString() // or "token_type"

                // Require: subject present AND token type is explicitly "access"
                if (!id.isNullOrBlank() && tokenType == "access") {
                    JWTPrincipal(payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                val authHeader = call.request.parseAuthorizationHeader()
                if (authHeader is HttpAuthHeader.Single && authHeader.authScheme.equals("Bearer", ignoreCase = true)) {
                    val token = authHeader.blob
                    try {
                        val decoded = JWT.decode(token)
                        val exp = decoded.expiresAt
                        if (exp != null && exp.before(Date())) {
                            call.respond(HttpStatusCode.Unauthorized, "Token has expired")
                        } else {
                            // token decoded but verification failed (signature / claims / wrong tokenType)
                            call.respond(HttpStatusCode.Unauthorized, "Token invalid or missing required claims")
                        }
                    } catch (e: JWTDecodeException) {
                        call.respond(HttpStatusCode.Unauthorized, "Malformed token")
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Missing Bearer token")
                }
            }


        }
    }

    // --- Repositories ---
    // Instantiate repositories for various entities
    val carRepository = CarRepository()
    val userRepository = UserRepository()
    val bonusPointsRepository = BonusPointsRepository()
    val reservationRepository = ReservationRepository()
    val availabilitiesRepository = AvailabilitiesRepository()
    val ridesRepository = RidesRepository()
    val PhotosRepository = PhotoRepository()

    // --- Mock data ---
    // Generate test users and reservations, seed mock data (idempotent)
    generateUsersMock()
    generateReservationsMock()

    // --- Routing ---
    // Define API endpoints and serve static content
    routing {
        staticFiles("/photos", File("/app/photos"))

        carRouting(carRepository)
        reservationRouting(reservationRepository, availabilitiesRepository)
        AvailabilitiesRouting(availabilitiesRepository)
        RidesRouting(ridesRepository)
        userRouting(userRepository)
        authRouting(userRepository)
        bonusPointsRouting(bonusPointsRepository)
        photosRouting(PhotosRepository)
    }

}