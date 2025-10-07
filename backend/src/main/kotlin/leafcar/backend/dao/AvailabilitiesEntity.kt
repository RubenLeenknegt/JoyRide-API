package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

import leafcar.backend.domain.Available

class AvailabilitiesEntity(id: EntityID<String>) : Entity<String>(id) {

    companion object : EntityClass<String, AvailabilitiesEntity>(AvailabilitiesTable)

    val carId by AvailabilitiesTable.carId

    val availableFrom by AvailabilitiesTable.availableFrom

    val availableTo: String? by AvailabilitiesTable.availableTo
}

/**
 * Converts this [ReservationEntity] (DAO) into a domain-level [Reservation] object.
 *
 * This allows the application to work with a pure domain model without exposing
 * database-specific details or Exposed internals.
 *
 * @return a [Reservation] domain object with the same data as this DAO.
 */
fun AvailabilitiesEntity.toDomain():Available  = Available(
    id = id.value,
    carId = carId,
    availableFrom = availableFrom,
    availableTo = availableTo
)