package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Database table schema for bonus points awarded to users for rides.
 *
 * This object defines the schema for the `BonusPoints` table in the database,
 * including its columns and relationships with other tables.
 *
 * @property id The primary key of the table, represented as a VARCHAR(36) (UUID).
 * @property userId A foreign key referencing the `UsersTable`'s ID column, with cascading delete behavior.
 * @property rideId A VARCHAR(36) column representing the ID of the associated ride.
 * @property points An INTEGER column representing the number of bonus points awarded.
 */
object BonusPointsTable : IdTable<String>("BonusPoints") {
    override val id = varchar("id", 36).entityId()
    val userId = reference("user_id", UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val rideId = reference("ride_id", RidesTable.id, onDelete = ReferenceOption.SET_NULL)
    val points = integer("points")
}