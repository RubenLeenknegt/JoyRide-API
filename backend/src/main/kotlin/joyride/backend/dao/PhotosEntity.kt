package joyride.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import joyride.backend.domain.Photo

/**
 * Exposed DAO entity representing a photo record in the database.
 *
 * Maps to [PhotosTable] and provides properties for optional associations with a car,
 * reservation, or user, along with the file path of the stored photo.
 *
 * @property carId Optional reference to the associated car.
 * @property reservationId Optional reference to the associated reservation.
 * @property userId Optional reference to the associated user.
 * @property filePath Path to the stored photo file.
 */

class PhotosEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, PhotosEntity>(PhotosTable)

    var carId by PhotosTable.carId
    var reservationId by PhotosTable.reservationId
    var userId by PhotosTable.userId
    var filePath by PhotosTable.filePath
}

/**
 * Extension function to convert a [PhotosEntity] to its domain model [Photo].
 *
 * Maps all entity properties to the domain data class, resolving optional foreign keys
 * to their underlying string values.
 */

fun PhotosEntity.toDomain(): Photo = Photo(
    id = this.id.value,
    carId = this.carId?.value,
    reservationId = this.reservationId?.value,
    userId = this.userId?.value,
    filePath = this.filePath
)