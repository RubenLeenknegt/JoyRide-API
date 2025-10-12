package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import leafcar.backend.repository.ReservationRepository

fun Route.reservationRouting(reservationRepository: ReservationRepository) {
    route("/reservations") {
        get {
            val reservations = reservationRepository.getAll()
            call.respond(status = HttpStatusCode.OK, reservations)
        }
    }
}
