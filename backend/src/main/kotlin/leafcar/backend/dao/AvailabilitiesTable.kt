package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AvailabilitiesTable : IdTable<String>("Availabilities") {
    override val id = varchar("id", 36).entityId()
    val carId = varchar("carId", 36).references(
        CarsTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val availableFrom = varchar("availableFrom", 50)
    val availableTo = varchar("availableTo", 19).nullable()
}