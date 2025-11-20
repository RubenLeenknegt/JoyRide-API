package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Exposed DAO table representing rides recorded in the system.
 *
 * @property id Unique identifier for the ride.
 * @property startX Starting X-coordinate of the ride.
 * @property startY Starting Y-coordinate of the ride.
 * @property endX Ending X-coordinate of the ride.
 * @property endY Ending Y-coordinate of the ride.
 * @property length Total distance of the ride in meters.
 * @property duration Duration of the ride in seconds.
 * @property reservationId Foreign key referencing [ReservationsTable.id]; deletes cascade when the reservation is removed.
 *
 * Each ride is linked to a specific reservation and stores both spatial and temporal data
 * describing the driven route.
 */

object RidesTable : IdTable<String>("Rides") {
    override var id = varchar("id", 36).entityId()

    var startX = float("start_x")

    var startY = float("start_y")

    var endX = float("end_x")

    var endY = float("end_y")

    var length = integer("length")

    var duration = integer("duration")

    var reservationId = varchar("reservation_id", 36).references(
        ReservationsTable.id,
        onDelete = ReferenceOption.CASCADE
    )
    override val primaryKey = PrimaryKey(id)
}
