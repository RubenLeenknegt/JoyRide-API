package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import leafcar.backend.domain.Photo

class PhotosEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, PhotosEntity>(PhotosTable)

    var carId by PhotosTable.carId
    var reservationId by PhotosTable.reservationId
    var userId by PhotosTable.userId
    var filePath by PhotosTable.filePath
}

fun PhotosEntity.toDomain(): Photo = Photo(
    id = this.id.value,
    carId = this.carId?.value,
    reservationId = this.reservationId?.value,
    userId = this.userId?.value,
    filePath = this.filePath
)