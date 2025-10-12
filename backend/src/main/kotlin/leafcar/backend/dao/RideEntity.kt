package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import leafcar.backend.domain.Ride

class RideEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, RideEntity>(RidesTable)

    val startX by RidesTable.startX

    val startY by RidesTable.startY

    val endX by RidesTable.endX

    val endY by RidesTable.endY

    val length by RidesTable.length

    val duration by RidesTable.duration

    val reservationId by RidesTable.reservationId
}

fun RideEntity.toDomain(): Ride = Ride(
    id = this.id.value,
    startX = this.startX,
    startY = this.startY,
    endX = this.endX,
    endY = this.endY,
    length = this.length,
    duration = this.duration,
    reservationId = this.reservationId
)
