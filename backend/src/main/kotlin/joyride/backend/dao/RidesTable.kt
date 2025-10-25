package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

object RidesTable : IdTable<String>("Rides") {
    override var id = varchar("id", 36).entityId()

    var startX = float("start_x")

    var startY = float("start_y")

    var endX = float("end_x")

    var endY = float("end_y")

    var length = integer("length")

    var duration = integer("duration")

    var reservationId = varchar("reservation_id", 36).references(
        ReservationsTable.id,
        onDelete = ReferenceOption.CASCADE
    )
}
