package leafcar.backend.dao

import leafcar.backend.domain.Color
import leafcar.backend.domain.FuelType
import leafcar.backend.domain.TransmissionType

object CarsTable : org.jetbrains.exposed.dao.id.IdTable<String>("Car") {
    override val id = varchar("id", 36).entityId()
    val brand = varchar("brand", 255)
    val model = varchar("model", 255)
    val buildYear = integer("buildYear")
    val transmissionType = enumerationByName("transmissionType",20, TransmissionType::class)
    val color = enumerationByName("color", 20, Color::class)
    val fuelType = enumerationByName("fuelType", 20, FuelType::class)
    val length = integer("length")
    val width = integer("width")
    val seats = integer("seats")
    val isofixCompatible = bool("isofixCompatible")
    val phoneMount = bool("phoneMount")
    val luggageSpace = double("luggageSpace")
    val parkingSensors = bool("parkingSensors")
    val locationX = float("locationX")
    val locationY = float("locationY")
}