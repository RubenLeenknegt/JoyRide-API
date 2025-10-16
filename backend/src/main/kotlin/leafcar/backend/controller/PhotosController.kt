package leafcar.backend.controller

import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.plugins.origin
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import leafcar.backend.repository.PhotoRepository
import io.ktor.server.request.receiveMultipart
import java.util.UUID

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

            val origin = call.request.origin
            val baseUrl = "${origin.scheme}://${origin.serverHost}:${origin.serverPort}"

            val photos = photoRepository.getPhotosByEntity(entityType, entityId, baseUrl)

            if (photos.isEmpty()) {
                return@get call.respond(HttpStatusCode.NotFound, "No photos found for $entityType $entityId")
            }

            call.respond(HttpStatusCode.OK, photos)
        }

        post("/{entityType}/{entityId}") { _: ApplicationCall ->
            val entityType = call.parameters["entityType"]
            val entityId = call.parameters["entityId"]

            if (entityType == null || entityId == null) {
                return@post call.respond(HttpStatusCode.BadRequest, "Missing entityType or entityId parameter.")
            }

            val validTypes = listOf("cars", "users", "rentals")
            if (entityType !in validTypes) {
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid photo type")
            }

            // Directory to store photos
            val uploadDir = File("photos/$entityType/$entityId")
            if (!uploadDir.exists()) uploadDir.mkdirs()

            val multipart = call.receiveMultipart()
            var uploadedCount = 0

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val originalFileName = part.originalFileName ?: "unnamed.jpg"
                    val extension = File(originalFileName).extension.ifEmpty { "jpg" }

                    // Generate a safe unique name to avoid overwriting
                    val fileName = "${UUID.randomUUID()}.$extension"
                    val file = File(uploadDir, fileName)

                    // Copy stream to file
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Save to database
                    val filePath = "photos/$entityType/$entityId/$fileName"
                    photoRepository.createPhoto(
                        entityType = entityType,
                        entityId = entityId,
                        filePath = filePath
                    )

                    uploadedCount++
                    part.dispose()
                } else {
                    part.dispose()
                }
            }

            if (uploadedCount == 0) {
                call.respond(HttpStatusCode.BadRequest, "No valid image files uploaded.")
            } else {
                call.respond(HttpStatusCode.Created, "$uploadedCount file(s) uploaded successfully.")
            }
        }
    }
}