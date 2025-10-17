package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.util.toMap
import leafcar.backend.dto.request.CarCreateOrUpdateRequest
import leafcar.backend.repository.CarRepository
import leafcar.backend.service.CarService

fun Route.carRouting(carRepository: CarRepository) {
    route("/cars") {

        // GET /cars?brand=BMW
        get {
            val params: Map<String, String> = call.request.queryParameters.toMap().mapValues { it.value.first() }
            val cars = carRepository.findWithFilters(params)

            if (cars.isEmpty())
                call.respond(HttpStatusCode.NotFound, "No cars found")
            else
                call.respond(HttpStatusCode.OK, cars)

        }

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

        // GET /cars/location
        get("location") {
            val cars = carRepository.getLocations()

            if (cars.isEmpty())
                call.respond(HttpStatusCode.NotFound, "No cars found")
            else
                call.respond(cars)
        }

//        authenticate("jwt") {
        post {
//                val principal = call.principal<JWTPrincipal>()
//                val userId = principal!!.payload.getClaim("userId").asString()
            val ownerId = "3fa85f64-5717-4562-b3fc-2c963f66a002"

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

        put("id/{id}") {
            val request = try {
                call.receive<CarCreateOrUpdateRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error bij bijwerken van DTO")
                return@put
            }

            val id = call.parameters["id"]
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
            val carService = CarService(carRepository)
            val updatedCar = carService.updateCar(request, id)

            if (updatedCar == null)
                call.respond(HttpStatusCode.InternalServerError, "Error bij opslaan in DB")
            else
                call.respond(HttpStatusCode.OK, updatedCar)
        }

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

    }
}