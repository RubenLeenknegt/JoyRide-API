package joyride.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

import joyride.backend.domain.Reservation

/**
 * DAO (Data Access Object) representing a row in the [ReservationTable].
 *
 * This class allows object-oriented access to reservation data in the database using Exposed.
 * Each instance corresponds to a single reservation, identified by its `id`.
 *
 * @property userId The ID of the user who made the reservation.
 * @property carId The ID of the car being reserved.
 * @property startDate The start date and time of the reservation.
 * @property endDate The end date and time of the reservation.
 */
class ReservationEntity(id: EntityID<String>) : Entity<String>(id) {

    companion object : EntityClass<String, ReservationEntity>(ReservationsTable)

    var userId by ReservationsTable.userId

    var carId by ReservationsTable.carId

    var startDate by ReservationsTable.startDate

    var endDate by ReservationsTable.endDate
}

/**
 * Converts this [ReservationEntity] (DAO) into a domain-level [Reservation] object.
 *
 * This allows the application to work with a pure domain model without exposing
 * database-specific details or Exposed internals.
 *
 * @return a [Reservation] domain object with the same data as this DAO.
 */
fun ReservationEntity.toDomain(): Reservation = Reservation(
    id = this.id.value,

    userId = this.userId,

    carId = this.carId,

    startDate = this.startDate,

    endDate = this.endDate
)
