package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AvailabilitiesTable : IdTable<String>("Availabilities") {
    override val id = varchar("id", 36).entityId()
    val carId = varchar("car_id", 36).references(
        CarsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val startDate = datetime("start_date")
    val endDate = datetime("end_date").nullable()
}