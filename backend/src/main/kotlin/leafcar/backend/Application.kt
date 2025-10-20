package leafcar.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import io.ktor.http.ContentType
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
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import leafcar.backend.domain.UserType
import leafcar.backend.services.AuthService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.auth.jwt.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.auth.parseAuthorizationHeader
import java.util.Date
import leafcar.backend.services.generateUsersMock
import leafcar.backend.services.generateReservationsMock

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Initialiseert Exposed met een Hikari-connection pool
    Database.connect(HikariDataSource(DatabaseConnection.getDataSource()))

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
    val backendRealm = dotenv["JWT_BACKEN_REALM"]

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

    // Seed mock data (idempotent)
    generateUsersMock()
    generateReservationsMock()

    routing {
        // Eenvoudige homepage met een link naar de JSON-output van /cars
        get("/") {
            val names: Array<String> = arrayOf("Giel van Gaal", "Ruben Leenkegt", "Ivar Visser")
            call.respondText(
                """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>P1 Avans CICD Kotlin/Ktor</title>
                </head>
                <body>
                    <h1>fantastic-lamp: A CI/CD pipeline for Kotlin and Ktor</h1>
                    <p>Hello, our names are: ${
                    names.joinToString(
                        separator = ", <br/>",
                        prefix = "<br/>",
                        postfix = "."
                    )
                }</p>
                    <a href="/cars">Bekijk alle auto's (JSON)</a><br/>
                    <a href="/reservations">Bekijk alle reservations (JSON)</a><br/>
                    <a href="/availabilities">Bekijk alle availabilities (JSON)</a><br/>
                    <a href="/rides">Bekijk alle rides (JSON)</a><br/>
                    <a href="/users">Bekijk alle User's (JSON)</a><br/>
                    <a href="/bonuspoints">Bekijk alles bonuspoints (JSON)</a><br/>
                    <br>
                    <br>
                    <a href="/photos/cars/4b285f64-5717-4562-b3fc-2c963f66b009">Bekijk fotos van Volkswagen Kever (JSON)</a><br/>
                </body>
                </html>
                """.trimIndent(),
                contentType = ContentType.Text.Html
            )
        }

        staticFiles("/photos", File("/app/photos"))

        // JSON endpoint(s) voor auto’s
        carRouting(carRepository)
        reservationRouting(reservationRepository)
        AvailabilitiesRouting(availabilitiesRepository)
        RidesRouting(ridesRepository)


        userRouting(userRepository)

        authRouting(userRepository)
        bonusPointsRouting(bonusPointsRepository)
        photosRouting(PhotosRepository)
//        Generate a set of test users
      

    }

}

//#### Tips en vervolgstappen
//- Configuratie scheiden: gebruik `application.conf`/HOCON of environment-variabelen (bijv. via `System.getenv`) voor host/poort en DB‑instellingen.
//- Health endpoints: voeg `GET /health` of `GET /ready` toe om readiness/liveness checks te ondersteunen in CI/CD en container‑omgevingen.
//- Error handling: installeer `StatusPages` om consistente foutresponses (JSON) te retourneren, inclusief logging/tracing.
//- OpenAPI: overweeg integratie met een OpenAPI/Swagger plugin of handmatige schema’s zodat clients jouw API eenvoudig kunnen verbruiken.
//- Security: voeg CORS, rate limiting en authenticatie toe zodra je endpoints publiek worden blootgesteld.