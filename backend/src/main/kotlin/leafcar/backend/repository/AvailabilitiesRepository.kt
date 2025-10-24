package leafcar.backend.repository

import kotlinx.datetime.LocalDateTime
import leafcar.backend.dao.AvailabilitiesEntity
import leafcar.backend.dao.AvailabilitiesTable
import leafcar.backend.dao.ReservationEntity
import leafcar.backend.dao.ReservationsTable
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Availability
import leafcar.backend.dto.request.AvailibilityCreateOrUpdate
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import org.jetbrains.exposed.sql.and



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
     * Returns all availability records that belong to a specific car.
     *
     * Executes the query inside an Exposed [transaction] and filters by the given car ID.
     *
     * @param carId The ID of the car whose availabilities you want to retrieve.
     * @return A list of [Availability] objects for the specified car.
     */
    fun getByCarId(carId: String): List<Availability> = transaction {
        AvailabilitiesEntity.find { AvailabilitiesTable.carId eq carId }
            .map { it.toDomain() }
    }

    /**
     * Retrieves all available time slots across all cars that are not covered by reservations.
     *
     * This function calculates availability gaps by subtracting overlapping reservation
     * ranges from the defined availabilities for each car. Partial overlaps are handled,
     * and remaining free time slots are returned as individual entries.
     *
     * Optionally filters the resulting time slots by a provided date range and/or car ID.
     *
     * @param startDate Optional start date filter. Only slots overlapping this range will be returned.
     * @param endDate Optional end date filter. Only slots overlapping this range will be returned.
     * @param carId Optional car ID filter. Only slots for this car will be returned.
     * @return A list of [AvailibilityCreateOrUpdate] objects representing available time slots.
     */
    fun getAvailableSlots( startDate: LocalDateTime? = null, endDate: LocalDateTime? = null, carId: String? = null ): List<AvailibilityCreateOrUpdate> = transaction {
        val result = mutableListOf<AvailibilityCreateOrUpdate>()

        // Fetch all availabilities from DB, optionally filtered by carId
        val availabilities = if (carId != null) {
            AvailabilitiesEntity.find { AvailabilitiesTable.carId eq carId }.toList()
        } else {
            AvailabilitiesEntity.all().toList()
        }

        for (availability in availabilities) {
            val currentCarId = availability.carId

            // Get all reservations for this car that overlap this availability
            val availabilityEnd = availability.endDate
            val reservations = if (availabilityEnd != null) {
                ReservationEntity.find {
                    (ReservationsTable.carId eq currentCarId) and
                            (ReservationsTable.startDate lessEq availabilityEnd) and
                            (ReservationsTable.endDate greaterEq availability.startDate)
                }
            } else {
                ReservationEntity.find {
                    (ReservationsTable.carId eq currentCarId) and
                            (ReservationsTable.endDate greaterEq availability.startDate)
                }
            }

            // Start with one initial free range: the full availability
            val freeRanges = mutableListOf(
                availability.startDate to availability.endDate
            )

            // Subtract each reservation from the free ranges
            for (reservation in reservations) {
                val newRanges = mutableListOf<Pair<LocalDateTime, LocalDateTime?>>()

                for ((freeStart, freeEnd) in freeRanges) {
                    // Reservation completely before or after the free slot → keep as is
                    if (reservation.endDate <= freeStart || (freeEnd != null && reservation.startDate >= freeEnd)) {
                        newRanges += freeStart to freeEnd
                    }
                    // Overlap at the start
                    else if (reservation.startDate <= freeStart && reservation.endDate < (freeEnd ?: reservation.endDate)) {
                        newRanges += reservation.endDate to freeEnd
                    }
                    // Overlap at the end
                    else if (reservation.startDate > freeStart && (freeEnd == null || reservation.endDate >= freeEnd)) {
                        newRanges += freeStart to reservation.startDate
                    }
                    // Reservation inside free range → split into two parts
                    else if (reservation.startDate > freeStart && (freeEnd != null && reservation.endDate < freeEnd)) {
                        newRanges += freeStart to reservation.startDate
                        newRanges += reservation.endDate to freeEnd
                    }
                }
                freeRanges.clear()
                freeRanges.addAll(newRanges)
            }

            // Apply optional filtering by provided date range
            val filteredRanges = freeRanges.mapNotNull { (slotStart, slotEnd) ->
                val startOk = startDate == null || slotEnd == null || slotEnd > startDate
                val endOk = endDate == null || slotStart < endDate
                if (startOk && endOk) slotStart to slotEnd else null
            }

            // Add the remaining free ranges to the result
            filteredRanges.forEach { (freeStart, freeEnd) ->
                result += AvailibilityCreateOrUpdate(
                    carId = currentCarId,
                    startDate = freeStart,
                    endDate = freeEnd
                )
            }
        }

        result
    }

    /**
     * Creates a new availability record in the database.
     *
     * @param carId The ID of the car this availability belongs to.
     * @param startDate The start date and time of the car's availability.
     * @param endDate The optional end date and time of the car's availability.
     * @return The created [Availability] domain object.
     */
    fun create( carId: String, startDate: LocalDateTime, endDate: LocalDateTime? ): Availability = transaction {
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
    fun update( id: String, carId: String, startDate: LocalDateTime, endDate: LocalDateTime? ): Availability? = transaction {
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

    /**
     * Checks if a car is available for the specified time period.
     *
     * This function queries the availabilities table to determine if there exists
     * at least one availability window that completely encompasses the requested
     * time period for the given car.
     *
     * @param carId The unique identifier of the car to check availability for
     * @param startDate The start date and time of the requested period
     * @param endDate The end date and time of the requested period
     * @return `true` if the car is available for the entire requested period,
     *         `false` otherwise
     *
     * @throws IllegalArgumentException if startDate is after endDate
     */
    fun withinAvailability( carId: String, startDate: LocalDateTime, endDate: LocalDateTime): Boolean = transaction {
        AvailabilitiesEntity.find {
            (AvailabilitiesTable.carId eq carId) and
                    (AvailabilitiesTable.startDate lessEq startDate) and
                    (AvailabilitiesTable.endDate greaterEq endDate)
        }.count() > 0
    }
}