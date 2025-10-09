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
import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.json.Json

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

    routing {
        // Eenvoudige homepage met een link naar de JSON-output van /cars
        get("/") {
            val names : Array<String> = arrayOf("Giel van Gaal", "Ruben Leenkegt", "Ivar Visser")
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
                    <p>Hello, our names are: ${names.joinToString(separator = ", <br/>", prefix = "<br/>", postfix = ".")}</p>
                    <a href="/cars">Bekijk alle auto's (JSON)</a>
                </body>
                </html>
                """.trimIndent(),
                contentType = ContentType.Text.Html
            )
        }

        // JSON endpoint(s) voor auto’s
        carRouting(carRepository)
    }
}

//#### Tips en vervolgstappen
//- Configuratie scheiden: gebruik `application.conf`/HOCON of environment-variabelen (bijv. via `System.getenv`) voor host/poort en DB‑instellingen.
//- Health endpoints: voeg `GET /health` of `GET /ready` toe om readiness/liveness checks te ondersteunen in CI/CD en container‑omgevingen.
//- Error handling: installeer `StatusPages` om consistente foutresponses (JSON) te retourneren, inclusief logging/tracing.
//- OpenAPI: overweeg integratie met een OpenAPI/Swagger plugin of handmatige schema’s zodat clients jouw API eenvoudig kunnen verbruiken.
//- Security: voeg CORS, rate limiting en authenticatie toe zodra je endpoints publiek worden blootgesteld.