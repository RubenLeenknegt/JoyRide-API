package leafcar.backend.repository

import leafcar.backend.dao.AvailabilitiesEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Available
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Repository for working with Availabilities.
 *
 * Provides methods to fetch, create, or update Availabilities from the database.
 */
class AvailabilitiesRepository {

    /**
     * Returns all Availabilities in the database as domain objects.
     *
     * @return List of [Availabilities] objects.
     */
    fun getAll(): List<Available> = transaction {
        AvailabilitiesEntity.all().map { it.toDomain() }
    }
}