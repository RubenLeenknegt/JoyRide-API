package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable

/**
 * Database table schema for bonus points awarded to users for rides.
 *
 * @property id Primary key (VARCHAR 36, typically UUID)
 * @property userId Foreign key referencing the user (VARCHAR 36)
 * @property rideId Foreign key referencing the ride (VARCHAR 36)
 * @property points Amount of bonus points awarded (INTEGER)
 */
object BonusPointsTable : IdTable<String>("BonusPoints") {
    /**
     * Primary key column for bonus point records.
     *
     * @see IdTable.id
     */
    override val id = varchar("id", 36).entityId()

    val userId = varchar("user_id", 36)

    val rideId = varchar("ride_id", 36)

    val points = integer("points")
}