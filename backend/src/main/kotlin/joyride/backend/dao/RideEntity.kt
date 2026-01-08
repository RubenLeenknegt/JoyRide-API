package joyride.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import joyride.backend.domain.Ride

/**
 * Exposed DAO entity representing a single ride record.
 *
 * Maps to [RidesTable] and stores spatial and temporal information about a ride
 * linked to a specific reservation.
 *
 * @property startX Starting X-coordinate of the ride.
 * @property startY Starting Y-coordinate of the ride.
 * @property endX Ending X-coordinate of the ride.
 * @property endY Ending Y-coordinate of the ride.
 * @property length Total distance between start and end point in meters.
 * @property duration Duration of the ride in seconds.
 * @property reservationId Identifier of the reservation this ride belongs to.
 * @property dateTimeStart Start date and time of the ride.
 * @property dateTimeEnd End date and time of the ride.
 * @property distanceTravelled Total distance travelled during the ride, expressed in kilometers.
 * @property name Optional human-readable name or label for the ride.
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

    var dateTimeStart by RidesTable.dateTimeStart

    var dateTimeEnd by RidesTable.dateTimeEnd

    var distanceTravelled by RidesTable.distanceTravelled

    var name by RidesTable.name
}

/**
 * Converts this [RideEntity] into its corresponding domain model [Ride].
 *
 * This function maps all persisted fields from the DAO entity into the domain layer,
 * translating database-backed values into a pure, persistence-agnostic representation.
 *
 * No validation or business logic is applied during this conversion.
 *
 * @return A [Ride] domain object containing the data represented by this entity.
 */

fun RideEntity.toDomain(): Ride = Ride(
    id = this.id.value,
    startX = this.startX,
    startY = this.startY,
    endX = this.endX,
    endY = this.endY,
    length = this.length,
    duration = this.duration,
    reservationId = this.reservationId,
    dateTimeStart = this.dateTimeStart,
    dateTimeEnd = this.dateTimeEnd,
    distanceTravelled = this.distanceTravelled,
    name = this.name
)

