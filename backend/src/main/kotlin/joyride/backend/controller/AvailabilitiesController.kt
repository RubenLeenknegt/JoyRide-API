package joyride.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import kotlinx.datetime.LocalDateTime
import joyride.backend.dto.request.AvailibilityCreateOrUpdate
import joyride.backend.repository.AvailabilitiesRepository

/**
 * Ktor routing for handling car availability endpoints.
 *
 * Provides authenticated CRUD operations and availability queries for cars.
 *
 * Routes:
 * - `GET /availabilities` → Retrieve all availabilities.
 * - `GET /availabilities/{id}` → Retrieve a single availability by its ID.
 * - `GET /availabilities/car/{carId}` → Retrieve all availabilities for a specific car.
 * - `GET /availabilities/available` → Retrieve all available slots without overlapping reservations.
 * - `GET /availabilities/available/{startDate}/{endDate}` → Retrieve available slots filtered by date range.
 * - `GET /availabilities/available/car/{carId}` → Retrieve available slots for a specific car.
 * - `POST /availabilities` → Create a new availability entry.
 * - `PUT /availabilities/{id}` → Update an existing availability entry by ID.
 * - `DELETE /availabilities/{id}` → Delete an availability entry by ID.
 *
 * All routes are protected with JWT authentication using the backend auth name from environment variables.
 *
 * @param AvailabilitiesRepository Repository providing access to availability data.
 */

fun Route.AvailabilitiesRouting(AvailabilitiesRepository: AvailabilitiesRepository) {

    // Authenticate routes using the JWT backend authentication name from environment variables
    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {

        route("/availabilities") {

            // GET all availabilities
            get {
                val Availabilities = AvailabilitiesRepository.getAll()
                call.respond(status = HttpStatusCode.OK, Availabilities)
            }

            // GET an availability by ID
            get("{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                val Availability = AvailabilitiesRepository.getById(id)

                if (Availability == null)
                    call.respond(HttpStatusCode.NotFound, "No availability with id $id")
                else
                    call.respond(HttpStatusCode.OK, Availability)
            }

            // GET an availability by carID
            get("/car/{carId}") {
                val carId = call.parameters["carId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing carId")

                val availabilities = AvailabilitiesRepository.getByCarId(carId)

                if (availabilities.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No availabilities found for carId $carId")
                else
                    call.respond(HttpStatusCode.OK, availabilities)
            }

            // GET all availabilities that do not have an overlapping reservation
            get("/available") {
                val availableSlots = AvailabilitiesRepository.getAvailableSlots(null, null)
                call.respond(HttpStatusCode.OK, availableSlots)
            }

            // GET all availabilities that do not have an overlapping reservation, includes filtering for a daterange
            get("/available/{startDate}/{endDate}") {
                val startDateParam = call.parameters["startDate"]
                val endDateParam = call.parameters["endDate"]

                val startDate = startDateParam?.let { LocalDateTime.parse(it) }
                val endDate = endDateParam?.let { LocalDateTime.parse(it) }

                val availableSlots = AvailabilitiesRepository.getAvailableSlots(startDate, endDate)
                call.respond(HttpStatusCode.OK, availableSlots)
            }

            // GET available slots for a specific car
            get("/available/car/{carId}") {
                val carId = call.parameters["carId"]
                val availableSlots = AvailabilitiesRepository.getAvailableSlots(null, null, carId)
                call.respond(HttpStatusCode.OK, availableSlots)
            }

            // POST new availability
            post {
                try {
                    val req = call.receive<AvailibilityCreateOrUpdate>()
                    // Create a new availability record using the repository
                    val created = AvailabilitiesRepository.create(
                        carId = req.carId,
                        startDate = req.startDate,
                        endDate = req.endDate
                    )
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: ContentTransformationException) {
                    // Handle invalid or missing JSON body
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing JSON body.")
                }
            }

            // PUT update an availability
            put("{id}") {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")

                try {
                    val req = call.receive<AvailibilityCreateOrUpdate>()
                    val updated = AvailabilitiesRepository.update(
                        id = id,
                        carId = req.carId,
                        startDate = req.startDate,
                        endDate = req.endDate
                    )

                    if (updated != null)
                        call.respond(HttpStatusCode.OK, updated)
                    else
                        call.respond(HttpStatusCode.NotFound, "No availability with id $id")
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing JSON body.")
                }
            }

            // DELETE an availability
            delete("{id}") {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")

                val deleted = AvailabilitiesRepository.delete(id)

                if (deleted)
                    call.respond(HttpStatusCode.NoContent)
                else
                    call.respond(HttpStatusCode.NotFound, "No availability with id $id")
            }

        }
    }
}
