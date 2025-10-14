package leafcar.backend.repository

import leafcar.backend.dao.PhotosTable
import leafcar.backend.domain.Photo
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class PhotoRepository {

    /**
     * Returns all photos for a given entity type and entity ID.
     *
     * @param entityType One of "cars", "users", "rentals"
     * @param entityId The UUID of the entity
     * @param baseUrl Optional base URL to prepend to file paths for client usage
     */
    fun getPhotosByEntity(entityType: String, entityId: String, baseUrl: String = ""): List<Photo> {
        return transaction {
            val column = when (entityType) {
                "cars" -> PhotosTable.carId
                "users" -> PhotosTable.userId
                "rentals" -> PhotosTable.reservationId
                else -> throw IllegalArgumentException("Invalid entity type: $entityType")
            }

            PhotosTable.select { column eq entityId }
                .map {
                    val filePath = it[PhotosTable.filePath]
                    Photo(
                        id = it[PhotosTable.id].value,
                        carId = if (entityType == "cars") entityId else null,
                        userId = if (entityType == "users") entityId else null,
                        reservationId = if (entityType == "rentals") entityId else null,
                        filePath = if (baseUrl.isNotEmpty()) "$baseUrl/$filePath" else filePath
                    )
                }

        }
    }
}
