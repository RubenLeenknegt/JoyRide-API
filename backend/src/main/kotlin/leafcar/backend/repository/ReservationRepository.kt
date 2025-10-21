package leafcar.backend.repository

import leafcar.backend.dao.ReservationEntity
import leafcar.backend.dao.ReservationsTable
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Reservation
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * Repository for working with reservations.
 *
 * Provides methods to fetch, create, or update reservations from the database.
 */
class ReservationRepository {

    /**
     * Returns all reservations in the database as domain objects.
     *
     * @return List of [Reservation] objects.
     */
    fun getAll(): List<Reservation> = transaction {
        ReservationEntity.all().map { it.toDomain() }
    }

    fun getReservationByUserId(userId: String) {
        ReservationsTable.select { ReservationsTable.userId like "%$userId%" }
    }

    fun getReservationByCarId(carId: String): List<Reservation> {
        return transaction {
            ReservationsTable.select { ReservationsTable.carId like "%$carId%" }
                .map { ReservationEntity.wrapRow(it).toDomain() }
        }

    }

    fun exists(userId: String, carId: String, startDate: LocalDateTime, endDate: LocalDateTime): Boolean = transaction {
        ReservationsTable.select {
            (ReservationsTable.userId eq userId) and
            (ReservationsTable.carId eq carId) and
            (ReservationsTable.startDate eq startDate) and
            (ReservationsTable.endDate eq endDate)
        }.any()
    }

    fun create(userId: String, carId: String, startDate: LocalDateTime, endDate: LocalDateTime): Reservation? = transaction {
        // prevent exact-duplicate reservation rows
        if (exists(userId, carId, startDate, endDate)) {
            return@transaction null
        }
        val newId = UUID.randomUUID().toString()
        ReservationsTable.insert { row ->
            row[ReservationsTable.id] = EntityID(newId, ReservationsTable)
            row[ReservationsTable.userId] = userId
            row[ReservationsTable.carId] = carId
            row[ReservationsTable.startDate] = startDate
            row[ReservationsTable.endDate] = endDate
        }
        // return the created row as domain
        ReservationEntity.findById(newId)?.toDomain()
    }
}
