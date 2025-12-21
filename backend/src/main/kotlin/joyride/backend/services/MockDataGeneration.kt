package joyride.backend.services

import joyride.backend.dao.AvailabilitiesTable
import joyride.backend.dao.BonusPointsEntity
import joyride.backend.dao.BonusPointsTable
import joyride.backend.dao.CarsTable
import joyride.backend.dao.PhotosTable
import joyride.backend.dao.ReservationsTable
import joyride.backend.dao.RidesTable
import joyride.backend.dao.UsersTable
import joyride.backend.domain.*
import joyride.backend.dto.request.CarCreateOrUpdateRequest
import joyride.backend.repository.AvailabilitiesRepository
import joyride.backend.repository.BonusPointsRepository
import joyride.backend.repository.CarRepository
import joyride.backend.repository.PhotoRepository
import joyride.backend.repository.ReservationRepository
import joyride.backend.repository.RidesRepository
import joyride.backend.repository.UserRepository
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.random.Random

object MockDataGeneration {
    val userRepository = UserRepository()
    val carRepository = CarRepository()
    val reservationRepo = ReservationRepository()
    val availabilitiesRepository = AvailabilitiesRepository()
    val ridesRepository = RidesRepository()
    val bonusPointsRepository = BonusPointsRepository()
    val photoRepository = PhotoRepository()
    val carService = CarService(carRepository)
    val createdAvailability: MutableList<Availability> = mutableListOf()
    val createdBonusPoints: MutableList<BonusPoints> = mutableListOf()
    val createdCars: MutableList<Car> = mutableListOf()
    val createdPhotos: MutableList<Photo> = mutableListOf()
    val createdReservations: MutableList<Reservation> = mutableListOf()
    val createdRides: MutableList<Ride> = mutableListOf()
    val createdUsers: MutableList<User> = mutableListOf()
    val createdOwners: MutableList<User> = mutableListOf()
    val createdRenters: MutableList<User> = mutableListOf()

    fun cleanup(database: Database) {
        transaction(database) {
            SchemaUtils.drop(
                UsersTable,
                CarsTable,
                ReservationsTable,
                AvailabilitiesTable,
                RidesTable,
                PhotosTable,
                BonusPointsTable
            )
            transaction(database) {
                SchemaUtils.create(
                    UsersTable,
                    CarsTable,
                    ReservationsTable,
                    AvailabilitiesTable,
                    RidesTable,
                    PhotosTable,
                    BonusPointsTable
                )
            }
        }
    }

    // Owner list numbers: 1,2,4,6,8
    // Renter list numbers 0,3,5,7,9
    fun generateUsersMock() {
        val users: List<List<String>> = listOf(
            listOf(
                "Eva",
                "de Groot",
                "1994-03-12",
                "eva.degroot@gmail.com",
                "hash11",
                "RENTER",
                "NL01BANK000000001",
                "Eva de Groot",
                "51.5878,4.7745"
            ),
            listOf(
                "Niels",
                "Verhoeven",
                "1988-11-07",
                "niels.verhoeven@outlook.com",
                "hash12",
                "OWNER",
                "NL02BANK000000002",
                "Niels Verhoeven",
                "51.4416,5.4697"
            ),
            listOf(
                "Sofia",
                "Rahmani",
                "1992-06-25",
                "sofia.rahmani@protonmail.com",
                "hash13",
                "OWNER",
                "NL03BANK000000003",
                "Sofia Rahmani",
                "52.3540,4.8922"
            ),
            listOf(
                "Daan",
                "Kuipers",
                "1997-02-19",
                "daan.kuipers@gmail.com",
                "hash14",
                "RENTER",
                "NL04BANK000000004",
                "Daan Kuipers",
                "51.5558,5.0913"
            ),
            listOf(
                "Lina",
                "Bosch",
                "1999-09-30",
                "lina.bosch@runbox.com",
                "hash15",
                "OWNER",
                "NL05BANK000000005",
                "Lina Bosch",
                "52.0790,4.3130"
            ),
            listOf(
                "Tom",
                "Visser",
                "1985-05-11",
                "tom.visser@outlook.com",
                "hash16",
                "RENTER",
                "NL06BANK000000006",
                "Tom Visser",
                "52.0905,5.1214"
            ),
            listOf(
                "Mila",
                "Peeters",
                "1993-07-08",
                "mila.peeters@protonmail.com",
                "hash17",
                "OWNER",
                "NL07BANK000000007",
                "Mila Peeters",
                "53.2194,6.5665"
            ),
            listOf(
                "Youssef",
                "Abidi",
                "1990-10-03",
                "youssef.abidi@gmail.com",
                "hash18",
                "RENTER",
                "NL08BANK000000008",
                "Youssef Abidi",
                "50.8514,5.6910"
            ),
            listOf(
                "Laura",
                "Willems",
                "1986-12-22",
                "laura.willems@outlook.com",
                "hash19",
                "OWNER",
                "NL09BANK000000009",
                "Laura Willems",
                "51.8426,5.8528"
            ),
            listOf(
                "Timo",
                "Smits",
                "2001-01-14",
                "timo.smits@gmail.com",
                "hash20",
                "RENTER",
                "NL10BANK000000010",
                "Timo Smits",
                "52.3840,4.6383"
            )
        )
        users.forEach { user ->
            val firstName = user[0]
            val lastName = user[1]
            val birthDate = user[2]
            val emailAddress = user[3]
            val password = user[4]
            val userTypeStr = user[5]
            if (userRepository.findByEmail(emailAddress) == null) {
                val passwordHashed = AuthService(userRepository).createPasswordHash(password)
                val currentUser = userRepository.createUser(
                    emailAddress = emailAddress,
                    passwordHash = passwordHashed,
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = LocalDate.parse(birthDate),
                    userType = UserType.valueOf(userTypeStr),
                )
                createdUsers.add(currentUser)
                when (currentUser.userType) {
                    UserType.OWNER -> createdOwners.add(currentUser)
                    UserType.RENTER -> createdRenters.add(currentUser)
                }
            }
        }
    }


    fun generateCarsMock() {
        val cars: List<List<String>> = listOf(
// List of
//  0: brand
//  1: model
//  2: build_year
//  3: transmission_type
//  4: color
//  5: fuel_type
//  6: length
//  7: width
//  8: seats
//  9: isofix_compatible
//  10: phone_mount
//  11: luggage_space
//  12: parking_sensors
//  13: location_x
//  14: location_y
//  15: license_plate
//  16: price_per_day
//  17: purchase_price
//  18: residual_value
//  19: usage_years
//  20: annual_km
//  21: energy_cost_per_km
//  22: maintenance_cost_per_km
//  23: average_consumption
            listOf(
                "Land Rover",
                "Series 3",
                "1975",
                "MANUAL",
                "GREEN",
                "ICE",
                "3880",
                "1600",
                "2",
                "FALSE",
                "FALSE",
                "200.0",
                "FALSE",
                "51.5652",
                "3.5912",
                "98-YA-67",
                "80.00",
                "25000.00",
                "15000.00",
                "5",
                "15000",
                "0.10",
                "0.05",
                "15"
            ),
            listOf(
                "Citroen",
                "GSA Pallas",
                "1980",
                "SEMI_AUTOMATIC",
                "BLUE",
                "ICE",
                "3800",
                "1650",
                "4",
                "FALSE",
                "FALSE",
                "220.0",
                "FALSE",
                "51.9853",
                "5.9112",
                "42-NZ-KR",
                "55.00",
                "18000.00",
                "12000.00",
                "5",
                "16000",
                "0.12",
                "0.06",
                "14"
            ),
            listOf(
                "BMW",
                "X5 drive 45e",
                "2021",
                "AUTOMATIC",
                "GREY",
                "ICE",
                "5000",
                "2000",
                "5",
                "TRUE",
                "TRUE",
                "650.0",
                "TRUE",
                "52.3702",
                "4.8952",
                "L-520-HB",
                "110.00",
                "65000.00",
                "50000.00",
                "5",
                "18000",
                "0.06",
                "0.05",
                "18"
            ),
            listOf(
                "Toyota",
                "Yaris 1.3 VVT-i",
                "2015",
                "MANUAL",
                "BLUE",
                "ICE",
                "3950",
                "1695",
                "5",
                "TRUE",
                "TRUE",
                "270.0",
                "FALSE",
                "52.6310",
                "4.7560",
                "G-148-BK",
                "60.00",
                "20000.00",
                "14000.00",
                "5",
                "17000",
                "0.11",
                "0.05",
                "15"
            ),
            listOf(
                "Audi",
                "A6 1.8 TFSI",
                "2016",
                "AUTOMATIC",
                "GREY",
                "ICE",
                "4700",
                "1850",
                "5",
                "FALSE",
                "TRUE",
                "500.0",
                "TRUE",
                "52.0705",
                "4.3007",
                "JR-900-R",
                "75.00",
                "30000.00",
                "22000.00",
                "5",
                "18000",
                "0.12",
                "0.06",
                "19"
            ),
            listOf(
                "Saab",
                "9000 2.3 Turbo CSE",
                "1996",
                "MANUAL",
                "BLACK",
                "ICE",
                "4700",
                "1800",
                "5",
                "TRUE",
                "TRUE",
                "450.0",
                "TRUE",
                "51.9470",
                "5.2310",
                "53-DZ-ZL",
                "85.00",
                "28000.00",
                "20000.00",
                "5",
                "16000",
                "0.11",
                "0.06",
                "18"
            ),
            listOf(
                "Suzuki",
                "Swift 1.3 GLX",
                "1998",
                "MANUAL",
                "RED",
                "ICE",
                "3700",
                "1680",
                "5",
                "FALSE",
                "FALSE",
                "300.0",
                "FALSE",
                "52.0920",
                "5.1040",
                "TP-TN-24",
                "50.00",
                "15000.00",
                "9000.00",
                "5",
                "14000",
                "0.13",
                "0.05",
                "17"
            ),
            listOf(
                "Chevrolet",
                "Corsica 2.2 LT",
                "1992",
                "MANUAL",
                "GREEN",
                "ICE",
                "4450",
                "1720",
                "5",
                "FALSE",
                "FALSE",
                "400.0",
                "FALSE",
                "51.9870",
                "5.9100",
                "FF-XN-82",
                "65.00",
                "22000.00",
                "15000.00",
                "5",
                "15000",
                "0.12",
                "0.06",
                "18"
            ),
            listOf(
                "Volkswagen",
                "Kever 1500",
                "1970",
                "MANUAL",
                "BLACK",
                "ICE",
                "4000",
                "1550",
                "2",
                "FALSE",
                "FALSE",
                "150.0",
                "FALSE",
                "52.0910",
                "5.1100",
                "DR-59-13",
                "45.00",
                "18000.00",
                "12000.00",
                "5",
                "13000",
                "0.13",
                "0.05",
                "17"
            ),
            listOf(
                "Austin",
                "A30",
                "1955",
                "MANUAL",
                "BEIGE",
                "ICE",
                "3500",
                "1500",
                "4",
                "FALSE",
                "FALSE",
                "120.0",
                "FALSE",
                "52.0900",
                "5.1200",
                "53-DZ-61",
                "40.00",
                "12000.00",
                "7000.00",
                "5",
                "12000",
                "0.14",
                "0.05",
                "19"
            ),
            listOf(
                "Trabant",
                "601",
                "1988",
                "MANUAL",
                "BLUE",
                "ICE",
                "3550",
                "1500",
                "4",
                "FALSE",
                "FALSE",
                "450",
                "FALSE",
                "52.0400",
                "4.1400",
                "AM-07-35",
                "50.00",
                "4900.00",
                "1200.00",
                "5",
                "12000",
                "0.14",
                "0.05",
                "17"
            )
        )
        var i = 0
        cars.forEach { car ->
            val currentCar = CarCreateOrUpdateRequest(

                brand = car[0],
                model = car[1],
                buildYear = car[2].toInt(),
                transmissionType = car[3],
                color = car[4],
                fuelType = car[5],
                length = car[6].toInt(),
                width = car[7].toInt(),
                seats = car[8].toInt(),
                isofixCompatible = car[9].equals("TRUE", ignoreCase = true),
                phoneMount = car[10].equals("TRUE", ignoreCase = true),
                luggageSpace = car[11].toDouble(),
                parkingSensors = car[12].equals("TRUE", ignoreCase = true),
                locationX = car[13].toFloat(),
                locationY = car[14].toFloat(),
                licensePlate = car[15],
                pricePerDay = car[16].toDouble(),
                purchasePrice = car[17].toDouble(),
                residualValue = car[18].toDouble(),
                usageYears = car[19].toInt(),
                annualKm = car[20].toInt(),
                energyCostPerKm = car[21].toDouble(),
                maintenanceCostPerKm = car[22].toDouble(),
                averageConsumption = car[23].toDouble()
            )
            val newCar = carService.createCar(currentCar, ownerId = createdOwners[i].id)
            i++
            if (createdOwners.count() <= i) {
                i = 0
            }
            createdCars.add(newCar!!)
        }

    }

    fun generateReservationsMock() {
        val listOfReservationDates: MutableList<List<LocalDateTime>> = mutableListOf()
        val now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today: LocalDate = now.date

        fun atHour(date: LocalDate, hour: Int) =
            LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, 0)

        // Add 2 reservations more than 5 days before now to list
        run {
            val d1 = today.plus(DatePeriod(days = -6))
            val start1 = atHour(d1, 9)
            val end1 = atHour(d1, 15)
            listOfReservationDates.add(listOf(start1, end1))
            val d2 = today.plus(DatePeriod(days = -8))
            val start2 = atHour(d2, 10)
            val end2 = atHour(d2, 14)
            listOfReservationDates.add(listOf(start2, end2))
        }
        // Add 2 reservations within +/- 5 days of now (one in the past few days, one upcoming) to list
        run {
            val d1 = today.plus(DatePeriod(days = -2))
            val start1 = atHour(d1, 9)
            val end1 = atHour(d1, 14)
            listOfReservationDates.add(listOf(start1, end1))

            val d2 = today.plus(DatePeriod(days = 3))
            val start2 = atHour(d2, 11)
            val end2 = atHour(d2, 18)
            listOfReservationDates.add(listOf(start2, end2))
        }

        // Add 2 reservations more than 5 days after now to list
        run {
            val d1 = today.plus(DatePeriod(days = 6))
            val start1 = atHour(d1, 8)
            val end1 = atHour(d1, 16)
            listOfReservationDates.add(listOf(start1, end1))

            val d2 = today.plus(DatePeriod(days = 10))
            val start2 = atHour(d2, 9)
            val end2 = atHour(d2, 15)
            listOfReservationDates.add(listOf(start2, end2))
        }

        //        id, user_id, car_id, start_date, end_date
        var i = 0
        createdCars.forEach { car ->
            listOfReservationDates.forEach { (start, end) ->
                val reservation = reservationRepo.createReservation(
                    userId = createdRenters[i].id,
                    carId = car.id,
                    startDate = start,
                    endDate = end
                )
                createdReservations.add(reservation)
            }
            i++
            if (i >= createdRenters.count()) {
                i = 0
            }
        }
    }

    fun generateAvailabilitiesMock() {
        val listOfAvailabilityPeriods: MutableList<List<LocalDateTime?>> = mutableListOf()
        val now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today: LocalDate = now.date

        fun atHour(date: LocalDate, hour: Int) =
            LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, 0)

        // Availability for next 30 days (8am-8pm)
        run {
            val start = atHour(today.plus(DatePeriod(days = 1)), 8)
            val end = atHour(today.plus(DatePeriod(days = 30)), 20)
            listOfAvailabilityPeriods.add(listOf(start, end))
        }

        // Availability for 31-60 days from now (8am-8pm)
        run {
            val start = atHour(today.plus(DatePeriod(days = 31)), 8)
            val end = atHour(today.plus(DatePeriod(days = 60)), 20)
            listOfAvailabilityPeriods.add(listOf(start, end))
        }

        // Open-ended availability starting 61 days from now
        run {
            val start = atHour(today.plus(DatePeriod(days = 61)), 8)
            listOfAvailabilityPeriods.add(listOf(start, null))
        }

        // Create availabilities for each car
        createdCars.forEach { car ->
            listOfAvailabilityPeriods.forEach { period ->
                availabilitiesRepository.create(
                    carId = car.id,
                    startDate = period[0]!!,
                    endDate = period.getOrNull(1)
                )
            }
        }
    }

    fun generateRidesMock() {
        data class RideCoords(val startX: Float, val startY: Float, val endX: Float, val endY: Float)

        val hardcodedRideCoords: List<RideCoords> = listOf(
            // Utrecht -> Amsterdam
            RideCoords(
                startX = 52.0905f,
                startY = 5.1214f,
                endX = 52.3720f,
                endY = 4.8952f
            ),
            // Eindhoven -> Nijmegen
            RideCoords(
                startX = 51.4416f,
                startY = 5.4697f,
                endX = 51.9853f,
                endY = 5.9112f
            ),
            // Groningen -> The Hague
            RideCoords(
                startX = 53.2194f,
                startY = 6.5665f,
                endX = 52.0790f,
                endY = 4.3130f
            )
        )
        val listOfRideDates: MutableList<List<LocalDateTime>> = mutableListOf()
        val now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today: LocalDate = now.date

        fun atHour(date: LocalDate, hour: Int) =
            LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, 0)

        // Ride 1: 30-40 days ago
        run {
            val d1 = today.plus(DatePeriod(days = -35))
            val start1 = atHour(d1, 9)
            val end1 = atHour(d1, 17)
            listOfRideDates.add(listOf(start1, end1))
        }

        // Ride 2: 15-20 days ago
        run {
            val d2 = today.plus(DatePeriod(days = -18))
            val start2 = atHour(d2, 10)
            val end2 = atHour(d2, 16)
            listOfRideDates.add(listOf(start2, end2))
        }

        // Ride 3: 5-10 days ago
        run {
            val d3 = today.plus(DatePeriod(days = -7))
            val start3 = atHour(d3, 8)
            val end3 = atHour(d3, 14)
            listOfRideDates.add(listOf(start3, end3))
        }

        // Create rides for each car (3 rides per car)
        var i = 0
        hardcodedRideCoords.forEach { rideCoords ->
            createdCars.forEach { car ->
                val ride = ridesRepository.create(
                    startX = rideCoords.startX,
                    startY = rideCoords.startY,
                    endX = rideCoords.endX,
                    endY = rideCoords.endY,
                    length = 100,
                    duration = 60,
                    reservationId = createdReservations.first { it.carId == car.id }.id
                )
                createdRides.add(ride)
                i++
                if (i >= 6) {
                    i = 0
                }

            }
        }
    }

    fun generateBonusPointsMock() {
        createdRides.forEach { ride ->
            val bonusPoints = bonusPointsRepository.create(
                points = Random.nextInt(),
                userId = createdReservations.first { it.id == ride.reservationId }.userId,
                rideId = ride.id
            )
            createdBonusPoints.add(bonusPoints!!)
        }
    }



    private val imageExts = setOf("jpg", "jpeg", "png", "webp", "gif")
    fun Path.isImageFile(): Boolean = Files.isRegularFile(this) && imageExts.contains(fileName.toString().substringAfterLast('.', "").lowercase())
    fun generatePhotosMock() {

        val base = Paths.get("photos")
        val carsDir = base.resolve("cars")
        val usersDir = base.resolve("users")

        if (!Files.isDirectory(carsDir)) return


        createdCars.withIndex().forEach { (idx, car) ->
            val carFolder = carsDir.resolve("car${idx + 1}")
            if (!Files.isDirectory(carFolder)) return@forEach

            val files = Files.list(carFolder).use { it.filter { p -> p.isImageFile() }.toList() }

            files.forEach { img ->
                // Use your enum/type for photos. If your project uses a different name, adjust `PhotoEntityType.CAR`.
                val photo = photoRepository.createPhoto(
                    entityType = "cars", // can be "cars", "users", "rentals"
                    entityId = car.id,
                    filePath = img.toString()
                )
                createdPhotos.add(photo)
            }
        }

        if (Files.isDirectory(usersDir)) {
            createdUsers.withIndex().forEach { (idx, user) ->
                val userFolder = usersDir.resolve("user${idx + 1}")
                if (!Files.isDirectory(userFolder)) return@forEach

                val files = Files.list(userFolder).use {
                    it.filter { p -> p.isImageFile() }.toList()
                }

                files.forEach { img ->
                    val photo = photoRepository.createPhoto(
                        entityType = "users",
                        entityId = user.id,
                        filePath = img.toString()
                    )
                    createdPhotos.add(photo)
                }
            }
        }
    }

}