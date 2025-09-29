package leafcar.backend.controller

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import leafcar.backend.repository.CarRepository

fun Route.carRouting(carRepository: CarRepository) {
    route("/cars") {
        val cars = carRepository.getAll()

        get {
            call.respond(cars)
        }
    }
}