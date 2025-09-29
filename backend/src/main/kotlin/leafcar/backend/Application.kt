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
//import leafcar.backend.repository.DatabaseConnection
import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariDataSource

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}
    fun Application.module() {
        Database.connect(HikariDataSource(DatabaseConnection.getDataSource()))

        install(ContentNegotiation) {
            json()
        }

        val carRepository = CarRepository()

        routing {
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

            // JSON endpoint
            carRouting(carRepository)
        }
    }