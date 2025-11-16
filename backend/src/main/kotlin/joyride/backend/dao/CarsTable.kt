package joyride.backend.dao

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Exposed table definition for car data.
 *
 * Maps the application's car-related domain model to the relational database.
 * Each column represents a property of a car, such as specifications, pricing,
 * and location data. Used as the persistence layer for [CarEntity].
 */
object CarsTable : IdTable<String>("Cars") { // Expliciete tabelnaam "Cars" meegegeven, hoeft eigenlijk niet maar voor duidelijkheid
    override val id = varchar("id", 36).entityId()
    val ownerId = reference("owner_id", UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val brand = varchar("brand", 255)
    val model = varchar("model", 255)
    val buildYear = integer("build_year")
    val transmissionType = varchar("transmission_type", 20)
    val color = varchar("color", 20)
    val fuelType = varchar("fuel_type", 20)
    val length = integer("length")
    val width = integer("width")
    val seats = integer("seats")
    val isofixCompatible = bool("isofix_compatible")
    val phoneMount = bool("phone_mount")
    val luggageSpace = double("luggage_space")
    val parkingSensors = bool("parking_sensors")
    val locationX = float("location_x")
    val locationY = float("location_y")
    val licensePlate = varchar("license_plate", 10)
    val pricePerDay = decimal("price_per_day", 10,2)
    val purchasePrice = decimal("purchase_price", 10, 2)
    val residualValue = decimal("residual_value", 10, 2)
    val usageYears = integer("usage_years")
    val annualKm = integer("annual_km")
    val energyCostPerKm = decimal("energy_cost_per_km", 10,2)
    val maintenanceCostPerKm = decimal("maintenance_cost_per_km", 10,2)
    val averageConsumption = decimal("average_consumption", 10,2)
    override val primaryKey = PrimaryKey(id)
}