package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.domain.Car
import leafcar.backend.dao.CarsTable
import leafcar.backend.repository.SharedRepository
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

    // APP-UC-03: Auto beheren
    fun create(car: Car): CarEntity? {
        return try {
            transaction {
                CarEntity.new(car.id) {
                    this.fromDomain(car)
                }
            }
        }
            catch(e: Exception) {
                null
            }
        }


    // APP-UC-03: Auto beheren
    fun update(car: Car): CarEntity? {
        return try {
            transaction {
                CarEntity[car.id].apply {
                    this.fromDomain(car)
                }
            }
        }
        catch(e: Exception) {
        null}
    }

    // APP-UC-03: Auto beheren
    fun delete(id: String) = transaction {
        CarEntity[id].delete()
    }

}