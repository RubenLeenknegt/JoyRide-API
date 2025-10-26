package joyride.backend.controller

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.HttpStatusCode
import joyride.backend.repository.BonusPointsRepository
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import joyride.backend.dto.request.BonusPointsCreate

// Load environment variables using dotenv
val dotenv = dotenv()

/**
 * Defines the routes for managing bonus points in the application.
 *
 * @param bonusPointsRepository The repository used to interact with bonus points data.
 */
fun Route.bonusPointsRouting(bonusPointsRepository: BonusPointsRepository) {
    // Define the base route for bonus points
    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {
        route("/bonuspoints") {
            // Authenticate routes using the JWT backend authentication name from environment variables
            /**
             * GET endpoint to retrieve all bonus points.
             * Responds with a list of all bonus points in the system.
             */
            get("/{userId}") {
                val userId = call.parameters["userId"].toString()
                val bonusPoints = bonusPointsRepository.getTotalPointsByUserId(userId) ?: call.respond(
                    HttpStatusCode.InternalServerError,
                    "Something went wrong processing your request"
                )
                call.respond(status = HttpStatusCode.OK, mapOf("totalPoints" to bonusPoints))
            }

            /**
             * POST endpoint to create new bonus points.
             * Accepts a JSON body containing the bonus points data and creates a new record.
             *
             * Responds with the created bonus points object or an error if the request body is invalid.
             */
            post {
                try {
                    val req = call.receive<BonusPointsCreate>()
                    // Create a new bonus points record using the repository
                    val created = bonusPointsRepository.create(
                        userId = req.userId,
                        rideId = req.rideId,
                        points = req.points
                    ) ?: call.respond(
                        HttpStatusCode.Forbidden,
                        "A point entry with rideId ${req.rideId} was already registered."
                    )

                    call.respond(HttpStatusCode.Created, created)
                } catch (e: ContentTransformationException) {
                    // Handle invalid or missing JSON body
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing JSON body.")
                }
            }
        }
    }
}