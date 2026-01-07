package joyride.backend.dao

import joyride.backend.domain.ReservationStatus
import joyride.backend.domain.UserType
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Exposed DAO table representing car reservations stored in the database.
 *
 * This table persists reservation data, including the associated user and car,
 * the reservation time window, and its current lifecycle status.
 *
 * @property id Unique identifier of the reservation (UUID stored as VARCHAR).
 * @property userId Identifier of the user who made the reservation.
 * This column enforces a foreign key constraint to [UsersTable.id] and cascades
 * deletes when the referenced user is removed.
 * @property carId Identifier of the car being reserved.
 * This column enforces a foreign key constraint to [CarsTable.id] and cascades
 * deletes when the referenced car is removed.
 * @property startDate Start date and time of the reservation.
 * @property endDate End date and time of the reservation.
 * @property status Current lifecycle status of the reservation, stored as a
 * string-backed enumeration.
 *
 * Each row represents a single reservation linking a user to a car for a defined
 * time window.
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

    val status = enumerationByName("status", 10, ReservationStatus::class)

    override val primaryKey = PrimaryKey(id)
}