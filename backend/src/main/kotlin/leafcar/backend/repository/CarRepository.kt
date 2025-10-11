package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.domain.Car
import leafcar.backend.dao.CarsTable
import org.example.leafcar.backend.repository.SharedRepository
import org.jetbrains.exposed.sql.transactions.transaction
import leafcar.backend.mappers.toCar
import leafcar.backend.mappers.toDomain

class CarRepository : SharedRepository<Car>(CarsTable, ::toCar) {

    fun getAll(): List<Car> = transaction {
        CarEntity.all().map { it.toDomain() }
    }
//
//    fun getById(id: String): List<Car> = transaction {
//        val car = CarEntity.findById(id)
//        if (car != null) listOf(car.toDomain()) else emptyList()
//    }
//
//    fun getByBrand(brand: String): List<Car> = transaction {
//        CarEntity.find { CarsTable.brand eq brand }.map { it.toDomain()}
//    }

            }