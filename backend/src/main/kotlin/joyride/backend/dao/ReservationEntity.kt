package joyride.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

import joyride.backend.domain.Reservation

/**
 * Exposed DAO entity representing a single reservation record.
 *
 * This entity provides object-oriented access to a row in [ReservationsTable] and
 * is used exclusively within the persistence layer.
 *
 * Foreign keys are represented as raw identifiers rather than navigable DAO
 * relationships.
 *
 * @property userId Identifier of the user who made the reservation.
 * @property carId Identifier of the car being reserved.
 * @property startDate Start date and time of the reservation.
 * @property endDate End date and time of the reservation.
 * @property status Current lifecycle status of the reservation.
 */
class ReservationEntity(id: EntityID<String>) : Entity<String>(id) {

    companion object : EntityClass<String, ReservationEntity>(ReservationsTable)

    var userId by ReservationsTable.userId

    var carId by ReservationsTable.carId

    var startDate by ReservationsTable.startDate

    var endDate by ReservationsTable.endDate

    var status by ReservationsTable.status
}

/**
 * Converts this [ReservationEntity] into its corresponding domain model [Reservation].
 *
 * This function maps all persisted fields from the DAO entity into a pure domain
 * representation, removing all database- and Exposed-specific concerns.
 *
 * Domain-level invariants (such as valid date ranges) are enforced by the
 * [Reservation] constructor.
 *
 * @return A [Reservation] domain object containing the data represented by this entity.
 */
fun ReservationEntity.toDomain(): Reservation = Reservation(
    id = this.id.value,

    userId = this.userId,

    carId = this.carId,

    startDate = this.startDate,

    endDate = this.endDate,

    status = this.status
)
