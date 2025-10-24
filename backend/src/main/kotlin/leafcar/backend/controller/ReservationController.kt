package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import kotlinx.datetime.LocalDateTime
import leafcar.backend.dto.request.ReservationCreateOrUpdateRequest
import leafcar.backend.repository.ReservationRepository
import leafcar.backend.repository.AvailabilitiesRepository

fun Route.reservationRouting(reservationRepository: ReservationRepository, availabilitiesRepository: AvailabilitiesRepository) {
    route("/reservations") {
        get {
            val reservations = reservationRepository.getAll()
            call.respond(status = HttpStatusCode.OK, reservations)
        }

        // GET by reservation ID
        get("{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

            val reservation = reservationRepository.getById(id)
            if (reservation == null)
                call.respond(HttpStatusCode.NotFound, "No reservation with id $id")
            else
                call.respond(HttpStatusCode.OK, reservation)
        }

        // GET by user ID
        get("user/{userId}") {
            val userId = call.parameters["userId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing userId")

            val reservations = reservationRepository.getByUserId(userId)
            if (reservations.isEmpty())
                call.respond(HttpStatusCode.NotFound, "No reservations found for user $userId")
            else
                call.respond(HttpStatusCode.OK, reservations)
        }

        // GET by car ID
        get("car/{carId}") {
            val carId = call.parameters["carId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing carId")

            val reservations = reservationRepository.getByCarId(carId)
            if (reservations.isEmpty())
                call.respond(HttpStatusCode.NotFound, "No reservations found for car $carId")
            else
                call.respond(HttpStatusCode.OK, reservations)
        }

        // POST new reservation, includes checking availability
        post {
            val request = call.receive<ReservationCreateOrUpdateRequest>()

            val startDate = LocalDateTime.parse(request.startDate)
            val endDate = LocalDateTime.parse(request.endDate)

            if (startDate > endDate) {
                return@post call.respond(HttpStatusCode.BadRequest, "Start date must be before end date")
            }

            val carId = request.carId
            val userId = request.userId

            // Step 1: Check availability (✅ now uses the injected instance)
            val isAvailable = availabilitiesRepository.withinAvailability(carId, startDate, endDate)
            if (!isAvailable) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "No availability found for car $carId during this time window"
                )
            }

            // Step 2: Check overlapping reservations
            val overlaps = reservationRepository.overlapsExistingReservation(carId, startDate, endDate)
            if (overlaps) {
                return@post call.respond(
                    HttpStatusCode.Conflict,
                    "This car already has a reservation during this time window"
                )
            }

            // Step 3: Create reservation
            val reservation = reservationRepository.createReservation(userId, carId, startDate, endDate)

            call.respond(HttpStatusCode.Created, reservation)
        }

        // PUT update existing reservation, includes checking availability
        put("{id}") {
            val id = call.parameters["id"]
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")

            val request = call.receive<ReservationCreateOrUpdateRequest>()
            val startDate = LocalDateTime.parse(request.startDate)
            val endDate = LocalDateTime.parse(request.endDate)

            if (startDate > endDate) {
                return@put call.respond(HttpStatusCode.BadRequest, "Start date must be before end date")
            }

            // Step 1: Check if reservation exists
            val existing = reservationRepository.getById(id)
                ?: return@put call.respond(HttpStatusCode.NotFound, "No reservation found with id $id")

            val carId = request.carId
            val userId = request.userId

            // Step 2: Check availability
            val isAvailable = availabilitiesRepository.withinAvailability(carId, startDate, endDate)
            if (!isAvailable) {
                return@put call.respond(
                    HttpStatusCode.BadRequest,
                    "No availability found for car $carId during this time window"
                )
            }

            // Step 3: Check overlapping reservations (ignore the same reservation ID)
            val overlaps = reservationRepository.overlapsExistingReservation(carId, startDate, endDate, excludeId = id)
            if (overlaps) {
                return@put call.respond(
                    HttpStatusCode.Conflict,
                    "This car already has a reservation during this time window"
                )
            }

            // Step 4: Update
            val updated = reservationRepository.updateReservation(id, userId, carId, startDate, endDate)

            if (updated == null) {
                call.respond(HttpStatusCode.NotFound, "No reservation found with id $id")
            } else {
                call.respond(HttpStatusCode.OK, updated)
            }
        }

        // DELETE existing reservation
        delete("{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")

            val deleted = reservationRepository.delete(id)

            if (deleted)
                call.respond(HttpStatusCode.NoContent)
            else
                call.respond(HttpStatusCode.NotFound, "No reservation with id $id")
        }

    }

        // TODO: Auth for my endpoints
        // TODO: availibile availibilities
        // TODO: get availibilties in timeslot filter
        // TODO: update photo endpoint
        // TODO: check code
        // TODO: kdock
}
