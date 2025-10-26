package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Exposed DAO table representing photos stored in the system.
 *
 * @property id Unique identifier for the photo.
 * @property carId Optional foreign key referencing [CarsTable]; deletes cascade when the car is removed.
 * @property reservationId Optional foreign key referencing [ReservationsTable]; deletes cascade when the reservation is removed.
 * @property userId Optional foreign key referencing [UsersTable]; deletes cascade when the user is removed.
 * @property filePath Path to the stored photo file.
 *
 * A photo can be associated with a car, a reservation, or a user, but typically only one association
 * is set per photo. The table enforces referential integrity with cascading deletes.
 */

object PhotosTable : IdTable<String>("Photos") {

    override val id = varchar("id", 36).entityId()

    val carId = reference(
        name = "car_id",
        foreign = CarsTable,
        onDelete = ReferenceOption.CASCADE
    ).nullable()

    val reservationId = reference(
        name = "reservation_id",
        foreign = ReservationsTable,
        onDelete = ReferenceOption.CASCADE
    ).nullable()

    val userId = reference(
        name = "user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).nullable()

    val filePath = varchar("file_path", 255)

    override val primaryKey = PrimaryKey(id)
}