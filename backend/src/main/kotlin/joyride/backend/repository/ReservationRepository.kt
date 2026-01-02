package joyride.backend.repository

import joyride.backend.dao.CarEntity
import joyride.backend.dao.PhotosEntity
import joyride.backend.dao.PhotosTable
import kotlinx.datetime.LocalDateTime
import joyride.backend.dao.ReservationEntity
import joyride.backend.dao.ReservationsTable
import joyride.backend.dao.toDomain
import joyride.backend.domain.Reservation
import joyride.backend.dto.response.ReservationList
import joyride.backend.utils.getCoverPhotoUrl
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
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
     * Retrieves a list of reservations for a specific user, enriched with car details and cover photos.
     *
     * Reservations are sorted in the following priority order:
     * 1. Active reservations (current date falls within the reservation period) - sorted by start date
     * 2. Upcoming reservations (start date is in the future) - sorted by start date (soonest first)
     * 3. Past reservations (end date is in the past) - sorted by end date (most recent first)
     *
     * This method fetches all reservations belonging to the specified user and enhances each reservation
     * with the corresponding car's brand, model, and cover photo URL. This combined data is optimized
     * for displaying reservation lists in the UI without requiring additional API calls.
     *
     *
     * @param userId The unique identifier of the user whose reservations should be retrieved.
     * @param baseUrl The base URL of the application, used to construct full URLs for cover photos.
     * @return A sorted list of [ReservationList] objects containing reservation data combined with car details.
     *         Returns an empty list if the user has no reservations.
     *
     * @see ReservationList
     * @see getByUserId
     */
    fun getReservationsList(userId: String, baseUrl: String): List<ReservationList> =
        transaction {
            val reservations = getByUserId(userId)
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val reservationList = reservations.map { reservation ->
                // Get the car details
                val car = CarEntity.findById(reservation.carId)

                val photoPath = getCoverPhotoUrl(reservation.carId)
                val coverPhotoUrl = photoPath?.let { "$baseUrl/$it" } ?: ""

                ReservationList(
                    id = reservation.id,
                    userId = reservation.userId,
                    carId = reservation.carId,
                    startDate = reservation.startDate,
                    endDate = reservation.endDate,
                    carBrand = car?.brand ?: "",
                    carModel = car?.model ?: "",
                    coverPhotoUrl = coverPhotoUrl
                )
            }

            // Split into categories
            val active = reservationList.filter { it.startDate <= now && it.endDate >= now }
                .sortedBy { it.startDate }

            val upcoming = reservationList.filter { it.startDate > now }
                .sortedBy { it.startDate }

            val past = reservationList.filter { it.endDate < now }
                .sortedByDescending { it.endDate }

            // Combine in desired order
            active + upcoming + past
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