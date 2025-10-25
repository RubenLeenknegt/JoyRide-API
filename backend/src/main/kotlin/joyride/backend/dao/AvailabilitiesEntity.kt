package joyride.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

import joyride.backend.domain.Availability
import kotlinx.datetime.LocalDateTime

class AvailabilitiesEntity(id: EntityID<String>) : Entity<String>(id) {

    companion object : EntityClass<String, AvailabilitiesEntity>(AvailabilitiesTable)

    var carId by AvailabilitiesTable.carId

    var startDate by AvailabilitiesTable.startDate

    var endDate: LocalDateTime? by AvailabilitiesTable.endDate
}

/**
 * Converts this [AvailabilitiesEntity] (DAO) into a domain-level [Availability] object.
 *
 * This allows the application to work with a pure domain model without exposing
 * database-specific details or Exposed internals.
 *
 * @return an [Availability] domain object with the same data as this DAO.
 */

fun AvailabilitiesEntity.toDomain():Availability  = Availability(
    id = id.value,
    carId = carId,
    startDate = startDate,
    endDate = endDate
)