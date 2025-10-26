package joyride.backend.controller

import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.origin
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import joyride.backend.repository.PhotoRepository
import io.ktor.server.request.receiveMultipart
import java.util.UUID

/**
 * Configures routing for photo management endpoints.
 *
 * Provides authenticated HTTP routes for uploading, retrieving, and deleting photos
 * associated with cars, users, or reservations.
 *
 * Supported operations:
 * - `GET /photos/{entityType}/{entityId}` → Retrieve all photos for a specific entity.
 * - `POST /photos/{entityType}/{entityId}` → Upload one or more photos for a specific entity.
 * - `DELETE /photos/{entityType}/{entityId}` → Delete all photos for a specific entity.
 *
 * Valid entity types are: `"cars"`, `"users"`, `"reservations"`.
 *
 * Uploaded files are stored under `photos/{entityType}/{entityId}/` on disk, and
 * corresponding database entries are created or deleted accordingly.
 *
 * @param photoRepository Repository providing access to photo data operations.
 */
fun Route.photosRouting(photoRepository: PhotoRepository) {

    // Authenticate routes using the JWT backend authentication name from environment variables
    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {

        route("/photos") {
            // Get all photos for a specific entity
            get("/{entityType}/{entityId}") {
                val entityType = call.parameters["entityType"]
                val entityId = call.parameters["entityId"]

                if (entityType == null || entityId == null) {
                    return@get call.respond(HttpStatusCode.BadRequest, "Missing entityType or entityId parameter.")
                }

                val validTypes = listOf("cars", "users", "reservations")
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

            // Post new photos to a specific entity
            post("/{entityType}/{entityId}") {
                val entityType = call.parameters["entityType"]
                val entityId = call.parameters["entityId"]

                if (entityType == null || entityId == null) {
                    return@post call.respond(HttpStatusCode.BadRequest, "Missing entityType or entityId parameter.")
                }

                val validTypes = listOf("cars", "users", "reservations")
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

            // Delete all photos for an entity
            delete("/{entityType}/{entityId}") {
                val entityType = call.parameters["entityType"]
                val entityId = call.parameters["entityId"]

                if (entityType == null || entityId == null) {
                    return@delete call.respond(HttpStatusCode.BadRequest, "Missing required parameters.")
                }

                val validTypes = listOf("cars", "users", "reservations")
                if (entityType !in validTypes) {
                    return@delete call.respond(HttpStatusCode.BadRequest, "Invalid photo type")
                }

                // Get all photos for this entity
                val photos = photoRepository.getPhotosByEntity(entityType, entityId)

                if (photos.isEmpty()) {
                    return@delete call.respond(HttpStatusCode.NotFound, "No photos found for this entity")
                }

                // Delete all files from disk
                photos.forEach { photo ->
                    val file = File(photo.filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                }

                // Delete from database
                photoRepository.deletePhotosByEntity(entityType, entityId)

                // Clean up empty directory
                val photoDir = File("photos/$entityType/$entityId")
                if (photoDir.exists() && photoDir.listFiles()?.isEmpty() == true) {
                    photoDir.delete()
                }

                call.respond(HttpStatusCode.OK, "${photos.size} photo(s) deleted successfully")
            }
        }
    }
}