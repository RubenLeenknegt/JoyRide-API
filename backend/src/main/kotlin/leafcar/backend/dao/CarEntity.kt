package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import leafcar.backend.domain.Car

class CarEntity(id: EntityID<String>) : org.jetbrains.exposed.dao.Entity<String>(id) {
    companion object : org.jetbrains.exposed.dao.EntityClass<String, CarEntity>(CarsTable)

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
}


fun CarEntity.toDomain(): Car = Car(
    id = this.id.value,
    brand = this.brand,
    model = this.model,
    buildYear = this.buildYear,
    transmissionType = this.transmissionType,
    color = this.color,
    fuelType = this.fuelType,
    length = this.length,
    width = this.width,
    seats = this.seats,
    isofixCompatible = this.isofixCompatible,
    phoneMount = this.phoneMount,
    luggageSpace = this.luggageSpace,
    parkingSensors = this.parkingSensors,
    locationX = this.locationX,
    locationY = this.locationY
)
