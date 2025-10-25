package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

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