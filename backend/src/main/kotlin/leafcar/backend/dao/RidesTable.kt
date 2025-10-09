package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object RidesTable : IdTable<String>("Rides") {
    override val id = varchar("id", 36).entityId()

    val startX = float("startX")

    val startY = float("startY")

    val endX = float("endX")

    val endY = float("endY")

    val length = integer("length")

    val duration = integer("duration")

    val reservationId = varchar("reservationId", 36).references(
        ReservationTable.id,
        onDelete = ReferenceOption.CASCADE
    )
}
