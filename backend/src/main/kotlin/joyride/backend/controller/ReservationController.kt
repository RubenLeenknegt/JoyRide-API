package joyride.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import kotlinx.datetime.LocalDateTime
import joyride.backend.dto.request.ReservationCreateOrUpdateRequest
import joyride.backend.repository.ReservationRepository
import joyride.backend.repository.AvailabilitiesRepository
import joyride.backend.utils.baseUrl
import joyride.backend.dto.request.UpdateReservationStatusRequest

/**
 * Configures routing for reservation-related endpoints.
 *
 * Provides authenticated HTTP routes for creating, retrieving, updating, and deleting
 * car reservations. Ensures that reservations respect car availability and avoid overlaps.
 *
 * Supported operations:
 * - `GET /reservations` → Retrieve all reservations.
 * - `GET /reservations/{id}` → Retrieve a specific reservation by ID.
 * - `GET /reservations/user/{userId}` → Retrieve all reservations for a specific user.
 * - `GET /reservations/user/reservationList/{userId}` → Retrieve all reservations for a specific user, return reservation along with data required for presentation in frontend.
 * - `GET /reservations/car/{carId}` → Retrieve all reservations for a specific car.
 * - `POST /reservations` → Create a new reservation (checks availability and overlapping reservations).
 * - `PUT /reservations/{id}` → Update an existing reservation (checks availability and overlapping reservations).
 * - `DELETE /reservations/{id}` → Delete a reservation by ID.
 *
 * All endpoints are secured using JWT authentication with the backend auth name.
 *
 * @param reservationRepository Repository providing access to reservation data.
 * @param availabilitiesRepository Repository used to check car availability during requested time windows.
 */
fun Route.reservationRouting(reservationRepository: ReservationRepository, availabilitiesRepository: AvailabilitiesRepository) {

    // Authenticate routes using the JWT backend authentication name from environment variables
    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {

        route("/reservations") {

            // GET all reservations
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

            // GET reservations with car details for a user
            get("user/reservationList/{userId}") {
                val userId = call.parameters["userId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing userId")

                val reservationsWithDetails = reservationRepository.getReservationsList(
                    userId = userId,
                    baseUrl = call.baseUrl()
                )

                if (reservationsWithDetails.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No reservations found for user $userId")
                else
                    call.respond(HttpStatusCode.OK, reservationsWithDetails)
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

                // Step 1: Check availability
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
                val overlaps =
                    reservationRepository.overlapsExistingReservation(carId, startDate, endDate, excludeId = id)
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

            // PUT update reservation status
            put("{id}/status") {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")

                val request = call.receive<UpdateReservationStatusRequest>()

                val updated = reservationRepository.updateStatus(id, request.status)

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
    }
}
