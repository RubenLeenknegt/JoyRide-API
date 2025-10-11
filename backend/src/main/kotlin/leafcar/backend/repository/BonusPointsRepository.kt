package leafcar.backend.repository

import leafcar.backend.domain.BonusPoints
import leafcar.backend.dao.BonusPointsEntity
import leafcar.backend.dao.toDomain
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class BonusPointsRepository {
    fun getAll(): List<BonusPoints> = transaction {
        BonusPointsEntity.all().map { it.toDomain() }
    }

    fun create(points: Int, userId: String, rideId: String): BonusPoints = transaction {
        val entity = BonusPointsEntity.new(UUID.randomUUID().toString()) {
            this.userId = userId
            this.rideId = rideId
            this.points = points
        }
        entity.toDomain()
    }
}
