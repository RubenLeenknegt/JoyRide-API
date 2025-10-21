package leafcar.backend

import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction

object EnvironmentSetup {

    fun setup() {

        val database = Database.connect(HikariDataSource(TestDatabaseConnection.getDataSource()))
        transaction(database) {
            SchemaUtils.drop(
                Users,
                Cars,
                Reservations,
                Availabilities,
                Rides,
                Photos,
                BonusPoints
            )
        }
        transaction(database) {
            SchemaUtils.create(
                Users,
                Cars,
                Reservations,
                Availabilities,
                Rides,
                Photos,
                BonusPoints
            )
        }
    }

    object Users : Table("Users") {
        val id = char("id", 36)
        val firstName = varchar("first_name", 50)
        val lastName = varchar("last_name", 50)
        val birthDate = date("birth_date").nullable()
        val emailAddress = varchar("email_address", 100).uniqueIndex()
        val passwordHash = varchar("password_hash", 255)
        val userType = varchar("user_type", 10)
        val bankAccount = varchar("bank_account", 20).nullable()
        val bankAccountName = varchar("bank_account_name", 20).nullable()
        val vehicleLocation = varchar("vehicle_location", 100).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object Cars : Table("Cars") {
        val id = char("id", 36)
        val ownerId = char("owner_id", 36).references(Users.id, onDelete = ReferenceOption.CASCADE).nullable()
        val brand = varchar("brand", 50)
        val model = varchar("model", 50)
        val buildYear = integer("build_year")
        val transmissionType = varchar("transmission_type", 20)
        val color = varchar("color", 20)
        val fuelType = varchar("fuel_type", 20)
        val length = integer("length").nullable()
        val width = integer("width").nullable()
        val seats = integer("seats").nullable()
        val isofixCompatible = bool("isofix_compatible").nullable()
        val phoneMount = bool("phone_mount").nullable()
        val luggageSpace = double("luggage_space").nullable()
        val parkingSensors = bool("parking_sensors").nullable()
        val locationX = float("location_x").nullable()
        val locationY = float("location_y").nullable()
        val licensePlate = varchar("license_plate", 10)
        val pricePerDay = decimal("price_per_day", 10, 2)
        val purchasePrice = decimal("purchase_price", 10, 2)
        val residualValue = decimal("residual_value", 10, 2)
        val usageYears = integer("usage_years")
        val annualKm = integer("annual_km")
        val energyCostPerKm = decimal("energy_cost_per_km", 10, 2)
        val maintenanceCostPerKm = decimal("maintenance_cost_per_km", 10, 2)
        val averageConsumption = decimal("average_consumption", 10, 2)

        override val primaryKey = PrimaryKey(id)
    }

    object Reservations : Table("Reservations") {
        val id = char("id", 36)
        val userId = char("user_id", 36).references(Users.id, onDelete = ReferenceOption.CASCADE).nullable()
        val carId = char("car_id", 36).references(Cars.id, onDelete = ReferenceOption.SET_NULL).nullable()
        val startDate = datetime("start_date")
        val endDate = datetime("end_date")

        override val primaryKey = PrimaryKey(id)
    }

    object Availabilities : Table("Availabilities") {
        val id = char("id", 36)
        val carId = char("car_id", 36).references(Cars.id, onDelete = ReferenceOption.CASCADE).nullable()
        val startDate = datetime("start_date")
        val endDate = datetime("end_date").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object Rides : Table("Rides") {
        val id = char("id", 36)
        val reservationId =
            char("reservation_id", 36).references(Reservations.id, onDelete = ReferenceOption.CASCADE).nullable()
        val startX = float("start_x").nullable()
        val startY = float("start_y").nullable()
        val endX = float("end_x").nullable()
        val endY = float("end_y").nullable()
        val length = integer("length").nullable()
        val duration = integer("duration").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object Photos : Table("Photos") {
        val id = char("id", 36)
        val carId = char("car_id", 36).references(Cars.id, onDelete = ReferenceOption.CASCADE).nullable()
        val reservationId =
            char("reservation_id", 36).references(Reservations.id, onDelete = ReferenceOption.CASCADE).nullable()
        val userId = char("user_id", 36).nullable()
        val filePath = varchar("file_path", 255)

        override val primaryKey = PrimaryKey(id)
    }

    object BonusPoints : Table("BonusPoints") {
        val id = char("id", 36)
        val userId = char("user_id", 36).references(Users.id, onDelete = ReferenceOption.CASCADE).nullable()
        val rideId = char("ride_id", 36).references(Rides.id, onDelete = ReferenceOption.SET_NULL).nullable()
        val points = integer("points")

        override val primaryKey = PrimaryKey(id)
    }
}