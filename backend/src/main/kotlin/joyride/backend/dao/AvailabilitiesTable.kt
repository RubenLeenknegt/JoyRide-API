package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Exposed DAO table representing car availabilities.
 *
 * @property id Unique identifier for the availability entry.
 * @property carId Foreign key referencing [CarsTable.id]; deletes cascade when the car is removed.
 * @property startDate Start date-time of the availability.
 * @property endDate Optional end date-time of the availability; can be null to indicate ongoing or open-ended availability.
 *
 * This table stores time windows during which a car is available for reservations.
 */

object AvailabilitiesTable : IdTable<String>("Availabilities") {
    override val id = varchar("id", 36).entityId()
    val carId = varchar("car_id", 36).references(CarsTable.id, onDelete = ReferenceOption.CASCADE)
    val startDate = datetime("start_date")
    val endDate = datetime("end_date").nullable()
    override val primaryKey = PrimaryKey(id)
}