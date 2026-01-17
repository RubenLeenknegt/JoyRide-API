package joyride.backend.repository

import joyride.backend.dao.PhotosTable
import joyride.backend.dao.PhotosEntity
import joyride.backend.dao.toDomain
import joyride.backend.domain.Photo
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import joyride.backend.dao.CarsTable
import joyride.backend.dao.UsersTable
import joyride.backend.dao.ReservationsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

/**
 * Repository for managing photo database operations.
 *
 * Supports creating, fetching, and deleting photos associated with:
 * - Cars
 * - Users
 * - Reservations
 */
class PhotoRepository {

    /**
     * Creates a new photo record in the database.
     *
     * @param entityType "cars", "users", or "reservations"
     * @param entityId UUID of the entity
     * @param filePath Path to the stored photo file
     */
    fun createPhoto(entityType: String, entityId: String, filePath: String): Photo {
        return transaction {
            PhotosEntity.new(UUID.randomUUID().toString()) {
                when (entityType) {
                    "cars" -> this.carId = EntityID(entityId, CarsTable)
                    "users" -> this.userId = EntityID(entityId, UsersTable)
                    "reservations" -> this.reservationId = EntityID(entityId, ReservationsTable)
                    else -> throw IllegalArgumentException("Invalid entity type: $entityType")
                }
                this.filePath = filePath
            }.toDomain()
        }
    }

    /**
     * Returns all photos for a given entity type and entity ID.
     *
     * @param entityType One of "cars", "users", "reservations"
     * @param entityId The UUID of the entity
     * @param baseUrl Optional base URL to prepend to file paths for client usage
     */
    fun getPhotosByEntity(entityType: String, entityId: String, baseUrl: String = ""): List<Photo> {
        return transaction {
            val column = when (entityType) {
                "cars" -> PhotosTable.carId
                "users" -> PhotosTable.userId
                "reservations" -> PhotosTable.reservationId
                else -> throw IllegalArgumentException("Invalid entity type: $entityType")
            }

            PhotosTable.select { column eq entityId }
                .map {
                    val filePath = it[PhotosTable.filePath]
                    Photo(
                        id = it[PhotosTable.id].value,
                        carId = if (entityType == "cars") entityId else null,
                        userId = if (entityType == "users") entityId else null,
                        reservationId = if (entityType == "reservations") entityId else null,
                        filePath = if (baseUrl.isNotEmpty()) "$baseUrl/$filePath" else filePath
                    )
                }
        }
    }

    /**
     * Deletes all photos for a given entity type and entity ID.
     *
     * @param entityType "cars", "users", or "reservations"
     * @param entityId UUID of the entity
     * @return number of rows deleted
     */
    fun deletePhotosByEntity(entityType: String, entityId: String): Int {
        return transaction {
            val column = when (entityType) {
                "cars" -> PhotosTable.carId
                "users" -> PhotosTable.userId
                "reservations" -> PhotosTable.reservationId
                else -> throw IllegalArgumentException("Invalid entity type: $entityType")
            }

            val table = when (entityType) {
                "cars" -> CarsTable
                "users" -> UsersTable
                "reservations" -> ReservationsTable
                else -> throw IllegalArgumentException("Invalid entity type: $entityType")
            }

            PhotosTable.deleteWhere { column.eq(EntityID(entityId, table)) }
        }
    }
}
