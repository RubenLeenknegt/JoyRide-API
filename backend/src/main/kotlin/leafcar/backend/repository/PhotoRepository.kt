package leafcar.backend.repository

import leafcar.backend.dao.PhotosTable
import leafcar.backend.dao.PhotosEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Photo
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import leafcar.backend.dao.CarsTable
import leafcar.backend.dao.UsersTable
import leafcar.backend.dao.ReservationsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class PhotoRepository {

    /**
     * Creates a new photo record in the database
     */
    fun createPhoto(entityType: String, entityId: String, filePath: String): Photo {
        return transaction {
            PhotosEntity.new(UUID.randomUUID().toString()) {
                when (entityType) {
                    "cars" -> this.carId = EntityID(entityId, CarsTable)
                    "users" -> this.userId = EntityID(entityId, UsersTable)
                    "rentals" -> this.reservationId = EntityID(entityId, ReservationsTable)
                }
                this.filePath = filePath
            }.toDomain()
        }
    }

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

    /**
     * Delete all photos for a given entity
     */
    fun deletePhotosByEntity(entityType: String, entityId: String): Int {
        return transaction {
            val column = when (entityType) {
                "cars" -> PhotosTable.carId
                "users" -> PhotosTable.userId
                "rentals" -> PhotosTable.reservationId
                else -> throw IllegalArgumentException("Invalid entity type: $entityType")
            }

            val table = when (entityType) {
                "cars" -> CarsTable
                "users" -> UsersTable
                "rentals" -> ReservationsTable
                else -> throw IllegalArgumentException("Invalid entity type: $entityType")
            }

            PhotosTable.deleteWhere { column.eq(EntityID(entityId, table)) }
        }
    }
}