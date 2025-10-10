package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable

/**
 * Represents the database table `BonusPoints`.
 *
 * This object defines the schema for bonus points storage. Each row in the table captures
 * information about a user's awarded bonus points for a specific ride.
 *
 * The table supports operations for storage and retrieval using the Exposed framework.
 */
object BonusPoints : IdTable<String>("BonusPoints") {
    /**
     * Represents the primary key column in the `CarsTable` database table.
     *
     * The variable is an Exposed `EntityID` generated using the `varchar` function,
     * which defines a column of type `VARCHAR` with a maximum length of 36 characters.
     * This column holds the unique identifier for a car entity, typically in the form of a UUID.
     *
     * Exposed uses this identifier to associate the `CarEntity` instances with their corresponding database rows.
     */
    override val id = varchar("id", 36).entityId()

    /**
     * Represents the unique identifier for a user, stored as a 36-character string.
     *
     * This column is defined in the table schema and typically holds a UUID value.
     * It ensures that each user can be uniquely identified in the system.
     */
    val userId = varchar("user_id", 36)

    /**
     * Represents the unique identifier for a ride in the system.
     *
     * This is a database column defined as a `VARCHAR` with a maximum length of 36 characters.
     * Typically, this identifier corresponds to a UUID to ensure global uniqueness across rides.
     *
     * Usage:
     * - Used as a primary or foreign key to reference ride records in the database.
     * - Ensures data integrity and uniqueness within the `ride_id` field.
     */
    val rideId = varchar("ride_id", 36)

    /**
     * Represents an integer column named `points` in the database table.
     *
     * This property is typically used to store point-related data associated with an entity.
     * It maps directly to a column in the respective table through the Exposed framework.
     *
     * Design considerations:
     * - Can be used to track scores, rewards, or other point-based metrics.
     * - Read-only by default unless otherwise configured for mutability in the corresponding entity class.
     */
    val points = integer("points")
}