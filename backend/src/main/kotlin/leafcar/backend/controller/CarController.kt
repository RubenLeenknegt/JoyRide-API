package leafcar.backend.controller

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
import leafcar.backend.dto.request.CarCreateOrUpdateRequest
import leafcar.backend.repository.CarRepository
import leafcar.backend.repository.ReservationRepository
import leafcar.backend.services.CarService
import leafcar.backend.services.JwtConfig.dotenv
import java.time.LocalDateTime

fun Route.carRouting(carRepository: CarRepository) {


    route("/cars") {
        authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {
            // APP-UC-04: Auto zoeken
            // GET /cars?brand=BMW
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
            get("location") {
                val cars = carRepository.getLocations()

                if (cars.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "No cars found")
                else
                    call.respond(cars)
            }

            // APP-UC-11: Route opvragen
            // GET /cars/location/{id}
            get("location/{id}") {
                val carId = call.parameters["id"].toString()
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asString()
                val cars = carRepository.getLocations()
                val car = cars.find { it.id == carId } ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    "No car with id $carId found"
                )
                val reservations = ReservationRepository().getReservationByCarId(carId)
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
