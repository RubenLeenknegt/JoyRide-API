package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Car
import leafcar.backend.dao.CarsTable
import leafcar.backend.dao.CarsTable.brand
import leafcar.backend.dao.CarsTable.transmissionType
import leafcar.backend.domain.Color
import leafcar.backend.domain.FuelType
import leafcar.backend.domain.TransmissionType
import org.example.leafcar.backend.repository.SharedRepository
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.JavaLocalDateColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalDateTimeColumnType
import java.time.LocalDate
import java.time.LocalDateTime

fun toCar(row: ResultRow): Car = Car (
    id = row[CarsTable.id].value,
    brand = row[CarsTable.brand],
    model = row[CarsTable.model],
    buildYear = row[CarsTable.buildYear],
    color = Color.valueOf(row[CarsTable.color]),
    fuelType = FuelType.valueOf(row[CarsTable.fuelType]),
    length = row[CarsTable.length],
    width = row[CarsTable.width],
    seats = row[CarsTable.seats],
    isofixCompatible = row[CarsTable.isofixCompatible],
    phoneMount = row[CarsTable.phoneMount],
    luggageSpace = row[CarsTable.luggageSpace],
    parkingSensors = row[CarsTable.parkingSensors],
    transmissionType = TransmissionType.valueOf(row[CarsTable.transmissionType]),
    locationX = row[CarsTable.locationX],
    locationY = row[CarsTable.locationY],
    licensePlate = row[CarsTable.licensePlate],
    pricePerDay = row[CarsTable.pricePerDay].toDouble(),
    purchasePrice = row[CarsTable.purchasePrice].toDouble(),
    residualValue = row[CarsTable.residualValue].toDouble(),
    usageYears = row[CarsTable.usageYears],
    annualKm = row[CarsTable.annualKm],
    energyCostPerKm = row[CarsTable.energyCostPerKm].toDouble(),
    maintenanceCostPerKm = row[CarsTable.maintenanceCostPerKm].toDouble(),
)

class CarRepository : SharedRepository<Car>(CarsTable, ::toCar) {

//    fun getAll(): List<Car> = transaction {
//        CarEntity.all().map { it.toDomain() }
//    }
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