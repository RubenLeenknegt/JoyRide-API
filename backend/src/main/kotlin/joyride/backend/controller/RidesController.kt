package joyride.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joyride.backend.repository.RidesRepository
import joyride.backend.dto.request.RideCreate
import io.ktor.server.auth.authenticate
import io.ktor.util.toMap
import joyride.backend.utils.baseUrl

/**
 * Configures routing for ride-related endpoints.
 *
 * Provides authenticated HTTP routes for creating, retrieving, and deleting rides
 * associated with reservations.
 *
 * Supported operations:
 * - `GET /rides` → Retrieve all rides.
 * - `GET /rides/{id}` → Retrieve a specific ride by ID.
 * - `GET /rides/reservation/{reservationId}` → Retrieve all rides for a specific reservation.
 * - `POST /rides` → Create a new ride entry.
 * - `DELETE /rides/{id}` → Delete a ride by ID.
 *
 * All endpoints are secured using JWT authentication with the backend auth name.
 *
 * @param ridesRepository Repository providing access to ride data operations.
 */

fun Route.RidesRouting(ridesRepository: RidesRepository) {

    // Authenticate routes using the JWT backend authentication name from environment variables
    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {

        route("/rides") {

            // GET all rides
            get {
                val rides = ridesRepository.getAll()
                call.respond(HttpStatusCode.OK, rides)
            }

            // GET rides by reservationId
            get("/reservation/{reservationId}") {
                val reservationId = call.parameters["reservationId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing reservationId")

                val rides = ridesRepository.getByReservationId(reservationId)

                if (rides.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No rides found for reservationId $reservationId")
                else
                    call.respond(HttpStatusCode.OK, rides)
            }

            // GET ride list by userId
            get("/ridelist/user/{userId}") {
                val userId = call.parameters["userId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing userId")

                val rideList = ridesRepository.getRideList(
                    userId = userId,
                    baseUrl = call.baseUrl()
                )

                if (rideList.isEmpty())
                    call.respond(HttpStatusCode.NoContent)
                else
                    call.respond(HttpStatusCode.OK, rideList)
            }

            // GET ride by id
            get("/{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                val ride = ridesRepository.getById(id)

                if (ride == null)
                    call.respond(HttpStatusCode.NotFound, "No ride with id $id")
                else
                    call.respond(HttpStatusCode.OK, ride)
            }

            // POST create ride
            post {
                try {
                    val req = call.receive<RideCreate>()

                    val created = ridesRepository.create(
                        startX = req.startX,
                        startY = req.startY,
                        endX = req.endX,
                        endY = req.endY,
                        length = req.length,
                        duration = req.duration,
                        reservationId = req.reservationId
                    )

                    call.respond(HttpStatusCode.Created, created)
                } catch (_: ContentTransformationException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid or missing JSON body."
                    )
                }
            }

            // DELETE ride by id
            delete("/{id}") {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")

                val deleted = ridesRepository.delete(id)

                if (deleted)
                    call.respond(HttpStatusCode.NoContent)
                else
                    call.respond(HttpStatusCode.NotFound, "No ride with id $id")
            }
        }
    }
}
