package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import leafcar.backend.repository.RidesRepository

fun Route.RidesRouting(ridesRepository: RidesRepository) {
    route("/rides") {
        get {
            val rides = ridesRepository.getAll()
            call.respond(HttpStatusCode.OK, rides)
        }
    }
}
