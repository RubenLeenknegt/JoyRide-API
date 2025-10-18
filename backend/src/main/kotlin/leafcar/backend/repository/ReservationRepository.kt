package leafcar.backend.repository

import leafcar.backend.dao.ReservationEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Reservation
import org.jetbrains.exposed.sql.transactions.transaction

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
}