package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.UUIDTable
import leafcar.backend.domain.*
object CarsTable : UUIDTable("Car") {
//    val id = uuid("id").autoGenerate()
    val brand = varchar("brand", 255)
    val model = varchar("model", 255)
    val buildYear = integer("buildYear")
    val transmissionType = enumeration("transmissionType", TransmissionType::class)
    val color = enumeration("color", Color::class)
    val fuelType = enumeration("fuelType", FuelType::class)
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