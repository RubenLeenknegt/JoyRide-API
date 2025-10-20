package leafcar.backend.repository

import leafcar.backend.domain.BonusPoints
import leafcar.backend.dao.BonusPointsEntity
import leafcar.backend.dao.UsersTable
import leafcar.backend.dao.toDomain
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * Repository class for managing bonus points data.
 *
 * This class provides methods to retrieve and create bonus points records
 * in the database using Exposed ORM.
 */
class BonusPointsRepository {

    /**
     * Retrieves all bonus points records from the database.
     *
     * @return A list of `BonusPoints` domain objects.
     */
    fun getAll(): List<BonusPoints> = transaction {
        BonusPointsEntity.all().map { it.toDomain() }
    }

    /**
     * Creates a new bonus points record in the database.
     *
     * @param points The number of bonus points to assign.
     * @param userId The ID of the user to whom the bonus points belong.
     * @param rideId The ID of the ride associated with the bonus points.
     * @return The created `BonusPoints` domain object.
     */
    fun create(points: Int, userId: String, rideId: String): BonusPoints = transaction {
        val entity = BonusPointsEntity.new(UUID.randomUUID().toString()) {
            this.userId = EntityID(userId, UsersTable)
            this.rideId = rideId
            this.points = points
        }
        entity.toDomain()
    }
}