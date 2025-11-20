package joyride.backend.dao

import joyride.backend.domain.BonusPoints
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Represents the BonusPoints entity in the database.
 *
 * This class maps to the `BonusPointsTable` and provides access to the
 * database fields for bonus points.
 *
 * @constructor Creates a new `BonusPointsEntity` with the given ID.
 * @property userId The ID of the user associated with the bonus points.
 * @property rideId The ID of the ride associated with the bonus points.
 * @property points The number of bonus points awarded.
 */
class BonusPointsEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, BonusPointsEntity>(BonusPointsTable)

    var userId by BonusPointsTable.userId
    var rideId by BonusPointsTable.rideId
    var points by BonusPointsTable.points
}

/**
 * Extension function to convert a `BonusPointsEntity` to its domain model representation.
 *
 * @return A `BonusPoints` object containing the data from the entity.
 */
fun BonusPointsEntity.toDomain(): BonusPoints = BonusPoints(
    id = this.id.value,
    userId = this.userId.value,
    rideId = this.rideId.value,
    points = this.points
)