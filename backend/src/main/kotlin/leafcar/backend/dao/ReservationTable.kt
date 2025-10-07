package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ReservationTable : IdTable<String>("Reservations") {
    override val id = varchar("id", 36).entityId()

    // Does not have a foreign key for now, user table does not exist at time of writing
    val userId = varchar("userId", 36)
    // add when user table exists!!!!!
//    val userId = varchar("user_id", 36).references(
//        UserTable.id,
//        onDelete = ReferenceOption.CASCADE
//    )

    val carId = varchar("carId", 36).references(
        CarsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val startDate = varchar("startDate", 50)
    val endDate = varchar("endDate", 50)
}