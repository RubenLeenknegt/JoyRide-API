package leafcar.backend.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.origin
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import leafcar.backend.repository.PhotoRepository

fun Route.photosRouting(photoRepository: PhotoRepository) {
    route("/photos") {

        get("/{entityType}/{entityId}") {
            val entityType = call.parameters["entityType"]
            val entityId = call.parameters["entityId"]

            if (entityType == null || entityId == null) {
                return@get call.respond(HttpStatusCode.BadRequest, "Missing entityType or entityId parameter.")
            }

            val validTypes = listOf("cars", "users", "rentals")
            if (entityType !in validTypes) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid photo type")
            }

            val photoDir = File("photos/$entityType/$entityId")
            if (!photoDir.exists() || !photoDir.isDirectory) {
                return@get call.respond(HttpStatusCode.NotFound, "No photos found for $entityType $entityId")
            }

            val origin = call.request.origin
            val baseUrl = "${origin.scheme}://${origin.serverHost}:${origin.serverPort}"

            val photos = photoRepository.getPhotosByEntity(entityType, entityId, baseUrl)
            call.respond(HttpStatusCode.OK, photos)
        }
    }
}


