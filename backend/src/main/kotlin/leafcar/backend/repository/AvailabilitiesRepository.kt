package leafcar.backend.repository

import kotlinx.datetime.LocalDateTime
import leafcar.backend.dao.AvailabilitiesEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Availability
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID


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
    fun getAll(): List<Availability> = transaction {
        AvailabilitiesEntity.all().map { it.toDomain() }
    }

    /**
     * Returns a single [Availability] from the database by its ID.
     *
     * Executes the query inside an Exposed [transaction] and converts the found [AvailibilityEntity]
     * into its corresponding domain object.
     *
     * @param id The unique identifier of the Availibility to retrieve.
     * @return The [Availability] object if found, or `null` if no Availability exists with the given ID.
     */
    fun getById(id: String): Availability? = transaction {
        AvailabilitiesEntity.findById(id)?.toDomain()
    }

    /**
     * Creates a new availability record in the database.
     *
     * @param carId The ID of the car this availability belongs to.
     * @param startDate The start date and time of the car's availability.
     * @param endDate The optional end date and time of the car's availability.
     * @return The created [Availability] domain object.
     */
    fun create(
        carId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime?
    ): Availability = transaction {
        val entity = AvailabilitiesEntity.new(UUID.randomUUID().toString()) {
            this.carId = carId
            this.startDate = startDate
            this.endDate = endDate
        }
        entity.toDomain()
    }

    /**
     * Updates an existing availability record in the database.
     *
     * Executes the update inside an Exposed [transaction]. If an availability
     * with the given ID exists, its fields are updated to the provided values.
     *
     * @param id The unique identifier of the availability to update.
     * @param carId The ID of the car this availability belongs to.
     * @param startDate The updated start date and time of the car's availability.
     * @param endDate The updated optional end date and time of the car's availability.
     * @return The updated [Availability] domain object, or `null` if no availability exists with the given ID.
     */
    fun update(
        id: String,
        carId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime?
    ): Availability? = transaction {
        val entity = AvailabilitiesEntity.findById(id)

        if (entity != null) {
            entity.apply {
                this.carId = carId
                this.startDate = startDate
                this.endDate = endDate
            }.toDomain()
        } else {
            null
        }
    }

    /**
     * Deletes an availability record from the database by its ID.
     *
     * Executes the delete operation inside an Exposed [transaction].
     * If an availability with the given ID exists, it is removed from the database.
     *
     * @param id The unique identifier of the availability to delete.
     * @return `true` if the record was successfully deleted, or `false` if no availability exists with the given ID.
     */
    fun delete(id: String): Boolean = transaction {
        val entity = AvailabilitiesEntity.findById(id)
        if (entity != null) {
            entity.delete()
            true
        } else {
            false
        }
    }



}