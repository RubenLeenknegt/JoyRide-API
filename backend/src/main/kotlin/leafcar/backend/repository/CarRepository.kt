package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.domain.Car
import leafcar.backend.dao.CarsTable
import leafcar.backend.repository.SharedRepository
import org.jetbrains.exposed.sql.transactions.transaction
import leafcar.backend.mappers.CarMapper.toDomain
import leafcar.backend.dto.request.*
import leafcar.backend.mappers.CarMapper
import leafcar.backend.mappers.CarMapper.toCarLocationRequest

class CarRepository : SharedRepository<Car>(CarsTable, CarMapper::toCar) {

    fun getAll(): List<Car> = transaction {
        CarEntity.all().map { it.toDomain() }
    }

    fun getById(id: String): List<Car> = transaction {
        val car = CarEntity.findById(id)
        if (car != null) listOf(car.toDomain()) else emptyList()
    }

    fun getLocations(): List<CarLocationRequest> = transaction {
        CarEntity.all().map { it.toCarLocationRequest() }
    }


            }