package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.domain.Car
import leafcar.backend.dao.CarsTable
import org.example.leafcar.backend.repository.SharedRepository
import org.jetbrains.exposed.sql.transactions.transaction
import leafcar.backend.mappers.CarMapper.toDomain
import leafcar.backend.dto.request.*
import leafcar.backend.mappers.CarMapper
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
            ownerId = car.ownerId
            brand = car.brand
            model = car.model
            buildYear = car.buildYear
            transmissionType = car.transmissionType.name
            color = car.color.name
            fuelType = car.fuelType.name
            length = car.length
            width = car.width
            seats = car.seats
            isofixCompatible = car.isofixCompatible
            phoneMount = car.phoneMount
            luggageSpace = car.luggageSpace
            parkingSensors = car.parkingSensors
            locationX = car.locationX
            locationY = car.locationY
            licensePlate = car.licensePlate
            pricePerDay = car.pricePerDay.toBigDecimal()
            purchasePrice = car.purchasePrice.toBigDecimal()
            residualValue = car.residualValue.toBigDecimal()
            usageYears = car.usageYears
            annualKm = car.annualKm
            energyCostPerKm = car.energyCostPerKm.toBigDecimal()
            maintenanceCostPerKm = car.maintenanceCostPerKm.toBigDecimal()
        }
    }

}