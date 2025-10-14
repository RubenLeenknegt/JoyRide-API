package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PhotosTable : IdTable<String>("Photos") {

    override val id = varchar("id", 36).entityId()

    val carId = varchar("car_id", 36).references(
        CarsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val reservationId = varchar("reservation_id", 36).references(
        ReservationsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val userId = varchar("user_id", 36).references(
        UsersTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val filePath = varchar("file_path", 255)
}