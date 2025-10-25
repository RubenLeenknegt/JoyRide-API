package joyride.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import joyride.backend.domain.Ride

class RideEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, RideEntity>(RidesTable)

    var startX by RidesTable.startX

    var startY by RidesTable.startY

    var endX by RidesTable.endX

    var endY by RidesTable.endY

    var length by RidesTable.length

    var duration by RidesTable.duration

    var reservationId by RidesTable.reservationId
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
