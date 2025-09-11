package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        routing {
            get("/") {
                val data = fetchData("SELECT * FROM users") // haalt users uit DB
                val userListHtml = data.joinToString("<br>") { "id=${it["id"]}, name=${it["name"]}" }
                val name: String = "Giel van Gaal"

                call.respondText(
                    """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>P1 Avans CICD Kotlin/Ktor</title>
            <style>
                body {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    margin: 0;
                    background: #f4f4f4;
                }
                .box {
                    padding: 20px;
                    background: white;
                    border-radius: 8px;
                    font-family: Arial, sans-serif;
                    font-size: 1.5rem;
                    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                }
            </style>
        </head>
        <body>
            <div class="box">
                fantastic-lamp: A CI/CD pipeline for Kotlin and Ktor<br />
                Jira / Git integration for projectmanagement<br /><br />
                Users:<br />
                <br />
                Database connection test: <br />
                $userListHtml<br />
                <br />
                Hello, my name is: $name<br />
            </div>
        </body>
        </html>
        """.trimIndent(),
                    contentType = io.ktor.http.ContentType.Text.Html
                )
            }
        }
    }.start(wait = true)
}