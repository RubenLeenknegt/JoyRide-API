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

/**
 * Startpunt van de applicatie: bootstrapt een embedded Ktor-server met de Netty engine.
 *
 * - Luistert op host `0.0.0.0` en poort `8080` (configureerbaar via code of environment).
 * - Roept `module()` aan om de Ktor-`Application` te configureren (plugins, routes, DI, etc.).
 */
fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

/**
 * Ktor `Application`-module: initialiseert databaseconnectie, installeert JSON-serialisatie
 * en registreert routes.
 *
 * Verantwoordelijkheden:
 * - Database: maakt verbinding via HikariCP (`HikariDataSource`) en Exposed (`Database.connect`).
 *   De daadwerkelijke DataSource-configuratie komt uit `DatabaseConnection.getDataSource()`.
 * - Content negotiation: installeert Kotlinx Serialization voor JSON met een aantal
 *   ontwikkelaarsvriendelijke opties (`prettyPrint`, `isLenient`, `ignoreUnknownKeys`).
 * - Routing: registreert een eenvoudige HTML-root (`GET /`) en het `cars`-endpoint via
 *   `carRouting`, waarbij een `CarRepository` wordt geïnjecteerd.
 *
 * Let op:
 * - In productie kun je `prettyPrint` en `isLenient` overwegen uit te zetten voor performance en strictere validatie.
 * - Overweeg configuratie (poort, host, DB-params) via environment-variabelen of een config-bestand te beheren.
 */
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
                // serializersModule = SerializersModule { contextual(UUID::class, UUIDSerializer) }
            }
        )
    }

    val carRepository = CarRepository()

    routing {
        // Eenvoudige homepage met een link naar de JSON-output van /cars
        get("/") {
            val name = "Giel van Gaal"
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
                    <p>Hello, my name is: $name</p>
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