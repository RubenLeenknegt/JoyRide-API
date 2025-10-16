package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.domain.Car
import leafcar.backend.dao.CarsTable
import org.example.leafcar.backend.repository.SharedRepository
import org.jetbrains.exposed.sql.transactions.transaction
import leafcar.backend.mappers.CarMapper.toDomain
import leafcar.backend.dto.request.*
import leafcar.backend.mappers.CarMapper
import leafcar.backend.mappers.CarMapper.fromDomain
import leafcar.backend.mappers.CarMapper.toCarLocationRequest

class CarRepository : SharedRepository<Car>(CarsTable, CarMapper::toCar) {

    // getAll() is absent because all() is already implemented in SharedRepository
    // which is called by the controller at /cars

    //
    fun getById(id: String): List<Car> = transaction {
        val car = CarEntity.findById(id)
        if (car != null) listOf(car.toDomain()) else emptyList()
    }

    // APP-UC-11: Route opvragen
    fun getLocations(): List<CarLocationRequest> = transaction {
        CarEntity.all().map { it.toCarLocationRequest() }
    }

    fun create(car: Car): CarEntity = transaction {
        CarEntity.new(car.id) {
            this.fromDomain(car)
        }
    }

    fun update(car: Car): CarEntity = transaction {
        CarEntity[car.id].apply {
            this.fromDomain(car)
        }
    }

}