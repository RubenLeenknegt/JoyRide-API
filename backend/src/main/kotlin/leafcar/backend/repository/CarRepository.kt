package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Car
import org.jetbrains.exposed.sql.transactions.transaction

class CarRepository {

    fun getAll(): List<Car> = transaction {
        CarEntity.all().map { it.toDomain() }
    }
}