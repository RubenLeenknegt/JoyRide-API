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
//import leafcar.backend.service.CarService.createCar


fun Route.carRouting(carRepository: CarRepository) {
    route("/cars") {

        // GET /cars?brand=BMW
        get {
            val params: Map<String, String> = call.request.queryParameters.toMap().mapValues { it.value.first() }
            val cars = carRepository.findWithFilters(params)
            call.respond(cars)
        }

        // GET /cars/id/{id}
        get("id/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

            val car = carRepository.getById(id)
            if (car.isEmpty()) call.respond(HttpStatusCode.NotFound, "No car with id $id")
            else call.respond(HttpStatusCode.OK, car.first())
        }

        // GET /cars/location
        get("location") {
            val cars = carRepository.getLocations()
            call.respond(cars)
        }

//        authenticate("jwt") {
            post {
//                val principal = call.principal<JWTPrincipal>()
//                val userId = principal!!.payload.getClaim("userId").asString()
                val ownerId = "3fa85f64-5717-4562-b3fc-2c963f66a002"
                val request = call.receive<CarCreateOrUpdateRequest>()
                val carService = CarService(carRepository)
                val newCar = carService.createCar(request, ownerId)
                call.respond(HttpStatusCode.Created, newCar)
            }

            put("id/{id}") {
                val request = call.receive<CarCreateOrUpdateRequest>()
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
                val carService = CarService(carRepository)
                val updatedCar = carService.updateCar(request, id)
                call.respond(HttpStatusCode.OK, updatedCar)
            }

            delete("id/{id}") {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")
                val carService = CarService(carRepository)
                val deleted = carService.deleteCar(id)
                if (deleted) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.NotFound, "No car with id $id")

            }

    }
}

//#### Tips en vervolgstappen
//- Validatie en foutafhandeling: bij toekomstige endpoints (bijv. `GET /cars/{id}` of `POST /cars`) documenteer je statuscodes (404/400/201) en foutformats in de KDoc van de handlerfunctie.
//- Paginering/filters: voor grote datasets kun je queryparameters ondersteunen (bijv. `?page=1&pageSize=20&brand=Toyota`). Documenteer de parameters en standaardwaarden in KDoc.
//- Versiebeheer: overweeg routes te namespacen (bijv. `/api/v1/cars`) en dit in de KDoc te vermelden.
//- Testbaarheid: omdat `carRouting` een pure extensie is met een geïnjecteerde repository, kun je in tests een fake/mock repository meegeven en via `testApplication {}` de responses asserten.
