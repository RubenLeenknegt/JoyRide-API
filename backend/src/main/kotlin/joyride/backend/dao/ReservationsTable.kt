package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Exposed DAO table representing car reservations in the database.
 *
 * @property id Unique identifier for the reservation.
 * @property userId Foreign key referencing [UsersTable.id]; deletes cascade when the user is removed.
 * @property carId Foreign key referencing [CarsTable.id]; deletes cascade when the car is removed.
 * @property startDate Start date and time of the reservation.
 * @property endDate End date and time of the reservation.
 *
 * Ensures that each reservation links a user to a specific car for a defined time window.
 */

object ReservationsTable : IdTable<String>("Reservations") {
    override val id = varchar("id", 36).entityId()

    val userId = varchar("user_id", 36).references(
        UsersTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val carId = varchar("car_id", 36).references(
        CarsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val startDate = datetime("start_date")
    val endDate = datetime("end_date")
    override val primaryKey = PrimaryKey(id)
}