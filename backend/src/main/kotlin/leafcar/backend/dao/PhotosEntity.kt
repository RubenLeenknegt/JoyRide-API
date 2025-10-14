package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

import leafcar.backend.domain.Photo

class PhotosEntity (id: EntityID<String>) : Entity<String>(id)  {
    companion object : EntityClass<String, PhotosEntity>(PhotosTable)

    val carId by PhotosTable.carId

    val reservationId by PhotosTable.reservationId

    val userId by PhotosTable.userId

    val filePath by PhotosTable.filePath
}

fun PhotosEntity.toDomain(): Photo = Photo(
    id = this.id.value,

    carId = this.carId,

    reservationId = this.reservationId,

    userId = this.userId,

    filePath = this.filePath
)