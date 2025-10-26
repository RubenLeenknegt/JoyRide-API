package joyride.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.util.toMap
import kotlinx.datetime.toJavaLocalDateTime
import joyride.backend.dto.request.CarCreateOrUpdateRequest
import joyride.backend.repository.CarRepository
import joyride.backend.repository.ReservationRepository
import joyride.backend.services.CarService
import joyride.backend.services.JwtConfig.dotenv
import java.time.LocalDateTime

/**
 * Configures routing for car-related endpoints.
 *
 * This function defines all HTTP routes for car operations including:
 * - Searching cars with filters
 * - Retrieving car details by ID
 * - Getting car locations
 * - Creating, updating and deleting cars
 * - Calculating TCO and cost per kilometer
 *
 * @param carRepository The repository for car data access operations
 */
fun Route.carRouting(carRepository: CarRepository) {


    route("/cars") {
        authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {
            // APP-UC-04: Auto zoeken
            // GET /cars?brand=BMW
            /**
             * Retrieves a list of cars filtered by query parameters.
             *
             * Supports filtering by various car attributes such as brand, model, etc.
             * Returns 404 if no cars match the filters.
             *
             * @return List of cars matching the filter criteria or 404 if none found
             */
            get {
                val params: Map<String, String> = call.request.queryParameters.toMap().mapValues { it.value.first() }
                val cars = carRepository.findWithFilters(params)

                if (cars.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No cars found")
                else
                    call.respond(HttpStatusCode.OK, cars)

            }

            // API-02 Cars endpoint
            // GET /cars/id/{id}
            /**
             * Retrieves a specific car by its unique identifier.
             *
             * @return The car details or 404 if the car doesn't exist
             */
            get("id/{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
                val car = carRepository.getById(id)

                if (car.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No car with id $id")
                else
                    call.respond(HttpStatusCode.OK, car.first())
            }

            // APP-UC-11: Route opvragen
            // GET /cars/location/
            /**
             * Retrieves the location coordinates of all cars.
             *
             * @return List of car locations or 404 if no cars are found
             */
            get("location") {
                val cars = carRepository.getLocations()

                if (cars.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No cars found")
                else
                    call.respond(cars)
            }

            // APP-UC-11: Route opvragen
            // GET /cars/location/{id}
            /**
             * Retrieves the location of a specific car.
             *
             * Access is restricted to users who have an active or recent reservation (within 5 days)
             * for the requested car. This prevents unauthorized location tracking.
             *
             * @return Car location if user has valid reservation, 404 otherwise
             */
            get("location/{id}") {
                val carId = call.parameters["id"].toString()
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asString()
                val cars = carRepository.getLocations()
                val car = cars.find { it.id == carId } ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    "No car with id $carId found"
                )
                val reservations = ReservationRepository().getByCarId(carId)
                val now = LocalDateTime.now()

                if (reservations.isNotEmpty()) {
                    reservations.forEach {
                        if (it.userId == userId &&
                            it.carId == carId &&
                            it.startDate.toJavaLocalDateTime().isAfter(now.minusDays(5))
                        ) {
                            call.respond(HttpStatusCode.OK, car)
                        }
                    }
                } else call.respond(
                    HttpStatusCode.NotFound,
                    "The combination userId and carId is not found or location was requested too early"
                )

            }


            // APP-UC-03: Auto beheren
            /**
             * Creates a new car in the system.
             *
             * The owner ID is automatically extracted from the JWT token of the authenticated user.
             * Validates the request body and returns the created car with generated ID.
             *
             * @return 201 Created with the new car object, 400 if request is invalid, 500 if database operation fails
             */
            post {
                val principal = call.principal<JWTPrincipal>()
                val ownerId = principal!!.payload.getClaim("id").asString()

                val request = try {
                    call.receive<CarCreateOrUpdateRequest>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error bij aanmaken van DTO")
                    return@post
                }

                val carService = CarService(carRepository)
                val newCar = carService.createCar(request, ownerId)

                if (newCar == null)
                    call.respond(HttpStatusCode.InternalServerError, "Error bij opslaan in DB")
                else
                    call.respond(HttpStatusCode.Created, newCar)
            }

            // APP-UC-03: Auto beheren
            /**
             * Updates an existing car's information.
             *
             * All car attributes can be modified except the ID and owner.
             * Returns the updated car object on success.
             *
             * @return 200 OK with updated car, 400 if ID is missing, 500 if update fails
             */
            put("id/{id}") {
                try {
                    val request = call.receive<CarCreateOrUpdateRequest>()

                    val id = call.parameters["id"]
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")

                    val carService = CarService(carRepository)
                    val updatedCar = carService.updateCar(request, id)

                    if (updatedCar == null) {
                        call.respond(HttpStatusCode.InternalServerError, "Error bij opslaan in DB")
                    } else {
                        call.respond(HttpStatusCode.OK, updatedCar)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Unknown error")))
                }
            }

            // APP-UC-03: Auto beheren
            /**
             * Deletes a car from the system.
             *
             * Permanently removes the car record. Associated data like reservations
             * should be handled by business logic before deletion.
             *
             * @return 200 OK if deleted successfully, 404 if car doesn't exist
             */
            delete("id/{id}") {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")
                val carService = CarService(carRepository)
                val deleted = carService.deleteCar(id)

                if (deleted)
                    call.respond(HttpStatusCode.OK)
                else
                    call.respond(HttpStatusCode.NotFound, "No car with id $id")
            }

            // APP-UC-09: TOC berekenen
            /**
             * Calculates the Total Cost of Ownership (TCO) for a specific car.
             *
             * TCO includes purchase price, depreciation, energy costs, maintenance costs
             * and other ownership expenses over the specified usage period.
             *
             * @return 200 OK with TCO calculation or 404 if calculation fails
             */
            get("tco/{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
                val carService = CarService(carRepository)
                val carTco = carService.getTco(id)

                if (carTco == null)
                    call.respond(HttpStatusCode.NotFound, "Er ging iets mis met de berekening van TCO")
                else
                    call.respond(HttpStatusCode.OK, carTco)
            }

            /**
             * Calculates the cost per kilometer (CPK) for a specific car.
             *
             * CPK combines all ownership and operational costs divided by expected annual kilometers,
             * providing insight into the actual driving cost per kilometer.
             *
             * @return 200 OK with CPK calculation or 404 if calculation fails
             */
            get("cpk/{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
                val carService = CarService(carRepository)
                val carCpk = carService.getCpk(id)

                if (carCpk == null)
                    call.respond(HttpStatusCode.NotFound, "Er ging iets mis met de berekening van Cost per KM")
                else {
                    call.respond(HttpStatusCode.OK, carCpk)
                }
            }
        }
    }
}
