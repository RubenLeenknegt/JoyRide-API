package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

class CarEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, CarEntity>(CarsTable)

    var ownerId by CarsTable.ownerId
    var brand by CarsTable.brand
    var model by CarsTable.model
    var buildYear by CarsTable.buildYear
    var transmissionType by CarsTable.transmissionType
    var color by CarsTable.color
    var fuelType by CarsTable.fuelType
    var length by CarsTable.length
    var width by CarsTable.width
    var seats by CarsTable.seats
    var isofixCompatible by CarsTable.isofixCompatible
    var phoneMount by CarsTable.phoneMount
    var luggageSpace by CarsTable.luggageSpace
    var parkingSensors by CarsTable.parkingSensors
    var locationX by CarsTable.locationX
    var locationY by CarsTable.locationY
    var licensePlate by CarsTable.licensePlate
    var pricePerDay by CarsTable.pricePerDay
    var purchasePrice by CarsTable.purchasePrice
    var residualValue by CarsTable.residualValue
    var usageYears by CarsTable.usageYears
    var annualKm by CarsTable.annualKm
    var energyCostPerKm by CarsTable.energyCostPerKm
    var maintenanceCostPerKm by CarsTable.maintenanceCostPerKm
    var averageConsumption by CarsTable.averageConsumption
}