package joyride.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import joyride.backend.domain.Ride

/**
 * Exposed DAO entity representing a ride record in the database.
 *
 * Maps to [RidesTable] and stores spatial and temporal information about a ride
 * linked to a specific reservation.
 *
 * @property startX Starting X-coordinate of the ride.
 * @property startY Starting Y-coordinate of the ride.
 * @property endX Ending X-coordinate of the ride.
 * @property endY Ending Y-coordinate of the ride.
 * @property length Total distance of the ride in meters.
 * @property duration Duration of the ride in seconds.
 * @property reservationId Identifier of the reservation this ride is associated with.
 */

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

/**
 * Extension function to convert a [RideEntity] to its domain model [Ride].
 *
 * Maps all entity properties to the domain data class.
 */

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
