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
    /**
     * Primary key column for the `BonusPoints` table.
     *
     * This column is a VARCHAR(36) and is typically used to store UUIDs.
     *
     * @see IdTable.id
     */
    override val id = varchar("id", 36).entityId()

    /**
     * Foreign key column referencing the `UsersTable`.
     *
     * This column stores the ID of the user associated with the bonus points
     * and enforces cascading delete behavior.
     */
    val userId = reference("user_id", UsersTable.id, onDelete = ReferenceOption.CASCADE)

    /**
     * Column representing the ID of the associated ride.
     *
     * This column is a VARCHAR(36) and stores the ride's unique identifier.
     */
    val rideId = varchar("ride_id", 36)

    /**
     * Column representing the number of bonus points awarded.
     *
     * This column is an INTEGER and stores the points associated with the record.
     */
    val points = integer("points")
}