package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object RidesTable : IdTable<String>("Rides") {
    override val id = varchar("id", 36).entityId()

    val startX = float("start_x")

    val startY = float("start_y")

    val endX = float("end_x")

    val endY = float("end_y")

    val length = integer("length")

    val duration = integer("duration")

    val reservationId = varchar("reservation_id", 36).references(
        ReservationsTable.id,
        onDelete = ReferenceOption.CASCADE
    )
}
