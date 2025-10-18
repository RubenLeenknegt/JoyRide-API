package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import leafcar.backend.repository.AvailabilitiesRepository

fun Route.AvailabilitiesRouting(AvailabilitiesRepository: AvailabilitiesRepository) {
    route("/availabilities") {
        get {
            val Availabilities = AvailabilitiesRepository.getAll()
            call.respond(status = HttpStatusCode.OK, Availabilities)
        }
    }
}
