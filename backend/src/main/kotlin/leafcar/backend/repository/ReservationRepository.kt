package leafcar.backend.repository

import kotlinx.datetime.LocalDateTime
import leafcar.backend.dao.ReservationEntity
import leafcar.backend.dao.ReservationsTable
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Reservation
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.and
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

    /**
     * Returns a reservation by its ID.
     *
     * @param id The unique reservation ID.
     * @return [Reservation] object if found, or null otherwise.
     */
    fun getById(id: String): Reservation? = transaction {
        ReservationEntity.findById(id)?.toDomain()
    }

    /**
     * Returns all reservations for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return List of [Reservation] objects belonging to the user.
     */
    fun getByUserId(userId: String): List<Reservation> = transaction {
        ReservationEntity.find { ReservationsTable.userId eq userId }
            .map { it.toDomain() }
    }

    /**
     * Returns all reservations for a specific car.
     *
     * @param carId The unique ID of the car.
     * @return List of [Reservation] objects for the car.
     */
    fun getByCarId(carId: String): List<Reservation> = transaction {
        ReservationEntity.find { ReservationsTable.carId eq carId }
            .map { it.toDomain() }
    }

    /**
     * Checks if a given time window overlaps with existing reservations for a car.
     *
     * @param carId The car ID to check.
     * @param start Start of the desired reservation window.
     * @param end End of the desired reservation window.
     * @param excludeId Optional reservation ID to exclude from the check (useful for updates).
     * @return True if there is an overlap, false otherwise.
     */
    fun overlapsExistingReservation( carId: String, start: LocalDateTime, end: LocalDateTime, excludeId: String? = null ): Boolean = transaction {
        val query = ReservationEntity.find {
            (ReservationsTable.carId eq carId) and
                    (
                        (ReservationsTable.startDate less end) and
                            (ReservationsTable.endDate greater start)
                    )
        }

        excludeId?.let { idToSkip ->
            query.any { it.id.value != idToSkip }
        } ?: query.any()
    }

    /**
     * Creates a new reservation.
     *
     * Generates a new UUID for the reservation ID.
     *
     * @param userId The user ID making the reservation.
     * @param carId The car ID being reserved.
     * @param startDate Start of the reservation.
     * @param endDate End of the reservation.
     * @return The created [Reservation] object.
     */
    fun createReservation(userId: String, carId: String, startDate: LocalDateTime, endDate: LocalDateTime): Reservation = transaction {
        ReservationEntity.new(UUID.randomUUID().toString()) {
            this.userId = userId
            this.carId = carId
            this.startDate = startDate
            this.endDate = endDate
        }.toDomain()
    }

    /**
     * Updates an existing reservation by ID.
     *
     * @param id The reservation ID to update.
     * @param userId The new user ID.
     * @param carId The new car ID.
     * @param startDate New start time.
     * @param endDate New end time.
     * @return Updated [Reservation] object if found, null otherwise.
     */
    fun updateReservation( id: String, userId: String, carId: String, startDate: LocalDateTime, endDate: LocalDateTime ): Reservation? = transaction {
        val entity = ReservationEntity.findById(id) ?: return@transaction null

        entity.userId = userId
        entity.carId = carId
        entity.startDate = startDate
        entity.endDate = endDate

        entity.toDomain()
    }

    /**
     * Deletes a reservation record from the database by its ID.
     *
     * Executes the delete operation inside an Exposed [transaction].
     * If a reservation with the given ID exists, it is removed from the database.
     *
     * @param id The unique identifier of the reservation to delete.
     * @return `true` if the record was successfully deleted, or `false` if no reservation exists with the given ID.
     */
    fun delete(id: String): Boolean = transaction {
        val entity = ReservationEntity.findById(EntityID(id, ReservationsTable))
        if (entity != null) {
            entity.delete()
            true
        } else {
            false
        }
    }

}