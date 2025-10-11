package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

import leafcar.backend.domain.Car
import leafcar.backend.domain.Color
import leafcar.backend.domain.FuelType
import leafcar.backend.domain.TransmissionType

class CarEntity(id: EntityID<String>) : Entity<String>(id) {
    /** Statische helper van Exposed om te kunnen query’en (bijv. `CarEntity.all()`). */
    companion object : EntityClass<String, CarEntity>(CarsTable)

    val brand by CarsTable.brand
    val model by CarsTable.model
    val buildYear by CarsTable.buildYear
    val transmissionType by CarsTable.transmissionType
    val color by CarsTable.color
    val fuelType by CarsTable.fuelType
    val length by CarsTable.length
    val width by CarsTable.width
    val seats by CarsTable.seats
    val isofixCompatible by CarsTable.isofixCompatible
    val phoneMount by CarsTable.phoneMount
    val luggageSpace by CarsTable.luggageSpace
    val parkingSensors by CarsTable.parkingSensors
    val locationX by CarsTable.locationX
    val locationY by CarsTable.locationY
    val licensePlate by CarsTable.licensePlate
    val pricePerDay by CarsTable.pricePerDay
    val purchasePrice by CarsTable.purchasePrice
    val residualValue by CarsTable.residualValue
    val usageYears by CarsTable.usageYears
    val annualKm by CarsTable.annualKm
    val energyCostPerKm by CarsTable.energyCostPerKm
    val maintenanceCostPerKm by CarsTable.maintenanceCostPerKm
}