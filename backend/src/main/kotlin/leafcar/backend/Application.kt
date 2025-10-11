package leafcar.backend

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import leafcar.backend.controller.*
import leafcar.backend.repository.CarRepository
import leafcar.backend.repository.UserRepository
import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariDataSource
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import leafcar.backend.domain.UserType
import leafcar.backend.repository.BonusPointsRepository
import leafcar.backend.service.Authentication

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

    val carRepository = CarRepository()
    val userRepository = UserRepository()
    val bonusPointsRepository = BonusPointsRepository()

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
                    <a href="/users">Bekijk alle User's (JSON)</a><br/>
                    <a href="/bonuspoints">Bekijk alles bonuspoints (JSON)</a><br/>
                    <p>Hello, our names are: ${
                    names.joinToString(
                        separator = ", <br/>",
                        prefix = "<br/>",
                        postfix = "."
                    )
                }</p>
                    <a href="/cars">Bekijk alle auto's (JSON)</a>
                    <a href="/users">Bekijk alle User's (JSON)>/a>
                </body>
                </html>
                """.trimIndent(),
                contentType = ContentType.Text.Html
            )
        }

        // JSON endpoint(s) voor auto’s
        carRouting(carRepository)
        userRouting(userRepository)
//        Generate a set of test users
        val users: List<List<String>> = listOf(
            listOf("Eva", "de Groot", "1994-03-12", "eva.degroot@gmail.com", "hash11", "RENTER"),
            listOf("Niels", "Verhoeven", "1988-11-07", "niels.verhoeven@outlook.com", "hash12", "OWNER"),
            listOf("Sofia", "Rahmani", "1992-06-25", "sofia.rahmani@protonmail.com", "hash13", "ADMIN"),
            listOf("Daan", "Kuipers", "1997-02-19", "daan.kuipers@gmail.com", "hash14", "RENTER"),
            listOf("Lina", "Bosch", "1999-09-30", "lina.bosch@runbox.com", "hash15", "OWNER"),
            listOf("Tom", "Visser", "1985-05-11", "tom.visser@outlook.com", "hash16", "RENTER"),
            listOf("Mila", "Peeters", "1993-07-08", "mila.peeters@protonmail.com", "hash17", "OWNER"),
            listOf("Youssef", "Abidi", "1990-10-03", "youssef.abidi@gmail.com", "hash18", "RENTER"),
            listOf("Laura", "Willems", "1986-12-22", "laura.willems@outlook.com", "hash19", "OWNER"),
            listOf("Timo", "Smits", "2001-01-14", "timo.smits@gmail.com", "hash20", "RENTER")
        )

        users.forEach { user ->
            val firstName = user[0]
            val lastName = user[1]
            val birthDate = user[2]
            val email = user[3]
            val password = user[4]
            val userTypeStr = user[5]
            if (userRepository.findByEmail(email) == null) {
              val passwordHashed =  Authentication(userRepository).createPasswordHash(password)
                userRepository.createUser(
                    emailAddress = email,
                    passwordHash = passwordHashed,
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = LocalDate.parse(birthDate),
                    userType = UserType.valueOf(userTypeStr),
                )
            }
        }

        bonusPointsRouting(bonusPointsRepository)
    }
}

//#### Tips en vervolgstappen
//- Configuratie scheiden: gebruik `application.conf`/HOCON of environment-variabelen (bijv. via `System.getenv`) voor host/poort en DB‑instellingen.
//- Health endpoints: voeg `GET /health` of `GET /ready` toe om readiness/liveness checks te ondersteunen in CI/CD en container‑omgevingen.
//- Error handling: installeer `StatusPages` om consistente foutresponses (JSON) te retourneren, inclusief logging/tracing.
//- OpenAPI: overweeg integratie met een OpenAPI/Swagger plugin of handmatige schema’s zodat clients jouw API eenvoudig kunnen verbruiken.
//- Security: voeg CORS, rate limiting en authenticatie toe zodra je endpoints publiek worden blootgesteld.