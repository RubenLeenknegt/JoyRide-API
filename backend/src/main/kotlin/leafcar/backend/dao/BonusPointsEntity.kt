package leafcar.backend.dao

import leafcar.backend.domain.BonusPoints
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BonusPointsEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, BonusPointsEntity>(BonusPointsTable)

    var userId by BonusPointsTable.userId
    var rideId by BonusPointsTable.rideId
    var points by BonusPointsTable.points
}
fun BonusPointsEntity.toDomain(): BonusPoints = BonusPoints(
    id = this.id.value,
    userId = this.userId,
    rideId = this.rideId,
    points = this.points
)