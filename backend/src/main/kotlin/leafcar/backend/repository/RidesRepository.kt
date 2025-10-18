package leafcar.backend.repository

import leafcar.backend.dao.RideEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Ride
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Repository for working with Availabilities.
 *
 * Provides methods to fetch, create, or update Availabilities from the database.
 */
class RidesRepository {

    /**
     * Returns all Availabilities in the database as domain objects.
     *
     * @return List of [Availabilities] objects.
     */
    fun getAll(): List<Ride> = transaction {
        RideEntity.all().map { it.toDomain() }
    }
}