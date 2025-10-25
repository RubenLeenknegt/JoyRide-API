package joyride.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import joyride.backend.controller.*
import joyride.backend.repository.*
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.util.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::Testmodule
    ).start(wait = true)
}

fun Application.Testmodule() {
    // Initialiseert Exposed met een Hikari-connection pool
    Database.connect(HikariDataSource(TestDatabaseConnection.getDataSource()))

    // JSON-serialisatie voor request/response-bodies met Kotlinx Serialization
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true              // leesbare output tijdens ontwikkeling
                isLenient = true                // tolereert licht afwijkende JSON
                ignoreUnknownKeys = true        // negeert extra velden in inkomende JSON

            }
        )
    }


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

    val carRepository = CarRepository()
    val userRepository = UserRepository()
    val bonusPointsRepository = BonusPointsRepository()
    val reservationRepository = ReservationRepository()
    val availabilitiesRepository = AvailabilitiesRepository()
    val ridesRepository = RidesRepository()
    val PhotosRepository = PhotoRepository()


    routing {
        staticFiles("/photos", File("/app/photos"))

        // JSON endpoint(s) voor auto’s
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
