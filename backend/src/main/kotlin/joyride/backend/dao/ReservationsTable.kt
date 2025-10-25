package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ReservationsTable : IdTable<String>("Reservations") {
    override val id = varchar("id", 36).entityId()

    val userId = varchar("user_id", 36).references(
        UsersTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val carId = varchar("car_id", 36).references(
        CarsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val startDate = datetime("start_date")
    val endDate = datetime("end_date")
}