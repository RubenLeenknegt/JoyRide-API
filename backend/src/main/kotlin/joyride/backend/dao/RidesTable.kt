package joyride.backend.dao

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Exposed DAO table representing rides recorded in the system.
 *
 * This table stores the persisted representation of a car ride, including spatial
 * coordinates, timing information, distance metrics, and a foreign key reference
 * to the reservation the ride belongs to.
 *
 * @property id Unique identifier of the ride (UUID stored as VARCHAR).
 * @property startX Starting X-coordinate of the ride.
 * @property startY Starting Y-coordinate of the ride.
 * @property endX Ending X-coordinate of the ride.
 * @property endY Ending Y-coordinate of the ride.
 * @property length Total distance between start and end point in meters.
 * @property duration Duration of the ride in seconds.
 * @property reservationId Identifier of the reservation this ride is associated with.
 * This column enforces a foreign key constraint to [ReservationsTable.id] and cascades
 * deletes when the referenced reservation is removed.
 * @property dateTimeStart Start date and time of the ride.
 * @property dateTimeEnd End date and time of the ride.
 * @property distanceTravelled Total distance travelled during the ride, expressed in kilometers.
 * @property name Optional human-readable name or label for the ride.
 *
 * Each ride is linked to exactly one reservation and captures both spatial and temporal
 * data describing the driven route.
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

    var dateTimeStart = datetime("date_time_start")

    var dateTimeEnd = datetime("date_time_end")

    var distanceTravelled = double("distance_travelled")

    var name = varchar("name", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
