package leafcar.backend.repository

import leafcar.backend.dao.RideEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Ride
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

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

    /**
     * Returns a single [Ride] from the database by its ID.
     *
     * Executes the query inside an Exposed [transaction] and converts the found [RideEntity]
     * into its corresponding domain object.
     *
     * @param id The unique identifier of the ride to retrieve.
     * @return The [Ride] object if found, or `null` if no ride exists with the given ID.
     */
    fun getById(id: String): Ride? = transaction {
        RideEntity.findById(id)?.toDomain()
    }

    /**
     * Creates a new ride record in the database.
     *
     * @param startX The X coordinate of the ride’s starting point.
     * @param startY The Y coordinate of the ride’s starting point.
     * @param endX The X coordinate of the ride’s endpoint.
     * @param endY The Y coordinate of the ride’s endpoint.
     * @param length The length of the ride in meters.
     * @param duration The duration of the ride in seconds.
     * @param reservationId The ID of the reservation associated with the ride.
     * @return The created [Ride] domain object.
     */
    fun create(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        length: Int,
        duration: Int,
        reservationId: String
    ): Ride = transaction {
        val entity = RideEntity.new(UUID.randomUUID().toString()) {
            this.startX = startX
            this.startY = startY
            this.endX = endX
            this.endY = endY
            this.length = length
            this.duration = duration
            this.reservationId = reservationId
        }
        entity.toDomain()
    }

    /**
     * Deletes a ride from the database by its unique ID.
     *
     * @param id The ID of the ride to delete.
     * @return `true` if a ride was deleted, `false` if no ride was found.
     */
    fun delete(id: String): Boolean = transaction {
        val entity = RideEntity.findById(id)
        if (entity != null) {
            entity.delete()
            true
        } else {
            false
        }
    }
}