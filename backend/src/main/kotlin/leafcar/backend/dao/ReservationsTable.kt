package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ReservationsTable : IdTable<String>("Reservations") {
    override val id = varchar("id", 36).entityId()

    val userId = varchar("userId", 36).references(
        UsersTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val carId = varchar("carId", 36).references(
        CarsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val startDate = varchar("startDate", 50)
    val endDate = varchar("endDate", 50)
}