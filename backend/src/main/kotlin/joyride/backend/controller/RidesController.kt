package joyride.backend.controller

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joyride.backend.repository.RidesRepository
import joyride.backend.dto.request.RideCreate
import io.ktor.server.auth.authenticate

fun Route.RidesRouting(ridesRepository: RidesRepository) {

    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {
        route("/rides") {

            // GET all rides
            get {
                val rides = ridesRepository.getAll()
                call.respond(HttpStatusCode.OK, rides)
            }

            // GET a ride by Id
            get("{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                val ride = ridesRepository.getById(id)

                if (ride == null)
                    call.respond(HttpStatusCode.NotFound, "No ride with id $id")
                else
                    call.respond(HttpStatusCode.OK, ride)
            }

            // GET a ride by reservationId
            get("/reservation/{reservationId}") {
                val reservationId = call.parameters["reservationId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing reservationId")

                val rides = ridesRepository.getByReservationId(reservationId)

                if (rides.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No rides found for reservationId $reservationId")
                else
                    call.respond(HttpStatusCode.OK, rides)
            }

            // POST a new ride
            post {
                try {
                    val req = call.receive<RideCreate>()
                    // Create a new ride record using the repository
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
                } catch (e: ContentTransformationException) {
                    // Handle invalid or missing JSON body
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing JSON body.")
                }
            }

            // DELETE a ride
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
