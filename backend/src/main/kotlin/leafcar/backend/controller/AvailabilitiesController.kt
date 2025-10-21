package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import leafcar.backend.domain.Availability
import leafcar.backend.dto.request.AvailibilityCreateOrUpdate
import leafcar.backend.dto.request.RideCreate
import leafcar.backend.repository.AvailabilitiesRepository

fun Route.AvailabilitiesRouting(AvailabilitiesRepository: AvailabilitiesRepository) {
    route("/availabilities") {
        get {
            val Availabilities = AvailabilitiesRepository.getAll()
            call.respond(status = HttpStatusCode.OK, Availabilities)
        }

        get("{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

            val Availability = AvailabilitiesRepository.getById(id)

            if (Availability == null)
                call.respond(HttpStatusCode.NotFound, "No availability with id $id")
            else
                call.respond(HttpStatusCode.OK, Availability)
        }

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
