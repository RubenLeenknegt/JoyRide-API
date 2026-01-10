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
import kotlin.math.roundToInt
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
            ),
            listOf(
                "Alexia",
                "Brooks",
                "1989-02-06",
                "Alexia.Brooks@gmail.com",
                "hash21",
                "RENTER",
                "NL10BANK000000011",
                "Alexia Brooks",
                "51.8999,4.8597"
            ),
            listOf(
                "Antonio",
                "Winters",
                "1997-05-01",
                "antonio.winters@gmail.com",
                "hash22",
                "RENTER",
                "NL10BANK000000012",
                "Antonio Winters",
                "52.6816,5.5593"
            ),
            listOf(
                "Safiya",
                "Webster",
                "1988-09-02",
                "safiya.webster@gmail.com",
                "hash23",
                "RENTER",
                "NL10BANK000000013",
                "Safiya Webster",
                "52.1122,4.7726"
            ),
            listOf(
                "Diego",
                "Hernandez",
                "1994-08-03",
                "diego.hernandez@gmail.com",
                "hash24",
                "RENTER",
                "NL10BANK000000014",
                "Diego Hernandez",
                "51.9466,4.7031"
            ),
            listOf(
                "Joseph",
                "Gonzalez",
                "1987-05-01",
                "joseph.gonzalez@gmail.com",
                "hash25",
                "RENTER",
                "NL10BANK000000015",
                "Joseph Gonzalez",
                "51.1752,6.1134"
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
            ),
            listOf(
                "Ferrari",
                "Testarossa",
                "1987",
                "MANUAL",
                "RED",
                "ICE",
                "4480",
                "1970",
                "2",
                "FALSE",
                "FALSE",
                "250",
                "FALSE",
                "51.9504",
                "5.2252",
                "13-BS-RB",
                "735.00",
                "179.500",
                "172.600",
                "5",
                "8000",
                "0.19",
                "0.15",
                "25"
            ),
            listOf(
                "Nissan",
                "Skyline R34",
                "1999",
                "MANUAL",
                "BLUE",
                "ICE",
                "4600",
                "1785",
                "4",
                "FALSE",
                "TRUE",
                "181",
                "FALSE",
                "52.1197",
                "5.2857",
                "04-VPN-4",
                "812.00",
                "210.000",
                "199.600",
                "8",
                "16000",
                "0.17",
                "0.15",
                "23"
            ),
            listOf(
                "DMC",
                "DeLorean",
                "1982",
                "MANUAL",
                "SILVER",
                "ICE",
                "4261",
                "1854",
                "2",
                "FALSE",
                "TRUE",
                "396",
                "FALSE",
                "52.8450",
                "5.7072",
                "TT-88-N",
                "156.00",
                "77.500",
                "55.212",
                "43",
                "1200",
                "0.12",
                "0.11",
                "16"
            ),
            listOf(
                "Nissan",
                "240Z",
                "1972",
                "MANUAL",
                "ORANGE",
                "ICE",
                "4140",
                "1620",
                "2",
                "FALSE",
                "FALSE",
                "296",
                "FALSE",
                "51.6500",
                "4.8537",
                "PM-77-34",
                "122",
                "59.000",
                "15",
                "17000",
                "8200",
                "0.17",
                "0.15",
                "32"
            ),
            listOf(
                "Mazda",
                "MX-5 NA",
                "1996",
                "MANUAL",
                "RED",
                "ICE",
                "3950",
                "1680",
                "2",
                "FALSE",
                "TRUE",
                "160",
                "FALSE",
                "51.7641",
                "5.5252",
                "HI-22_FRP",
                "55",
                "6500",
                "5200",
                "7",
                "18200",
                "0.15",
                "0.11",
                "16"
            ),
            listOf(
                "Mazda",
                "RX-7 FB",
                "1985",
                "MANUAL",
                "RED",
                "ICE",
                "4320",
                "1670",
                "2",
                "FALSE",
                "FALSE",
                "172",
                "FALSE",
                "53.0781",
                "7.3951",
                "B-999-AVX",
                "72",
                "12500",
                "9600",
                "15",
                "14000",
                "0.21",
                "0.26",
                "26"
            ),
            listOf(
                "Peel",
                "P-50",
                "2019",
                "AUTOMATIC",
                "BLUE",
                "BEV",
                "1371",
                "990",
                "1",
                "FALSE",
                "TRUE",
                "5",
                "FALSE",
                "53.1568",
                "4.8618",
                "62-D-167",
                "34",
                "13500",
                "9700",
                "6",
                "1536",
                "0.12",
                "0.06",
                "5"
            ),
            listOf(
                "Porsche",
                "Taycan",
                "2021",
                "AUTOMATIC",
                "WHITE",
                "BEV",
                "4960",
                "1960",
                "4",
                "TRUE",
                "TRUE",
                "488",
                "TRUE",
                "52.0753",
                "4.3088",
                "AM-986-D",
                "129",
                "52000",
                "49600",
                "4",
                "23000",
                "0.16",
                "0.25",
                "13"
            ),
            listOf(
                "BMW",
                "IX5 Hydrogen",
                "2025",
                "AUTOMATIC",
                "WHITE",
                "FCEV",
                "5312",
                "1989",
                "4",
                "TRUE",
                "TRUE",
                "1750",
                "TRUE",
                "52.0246",
                "4.8683",
                "ZD-261-K",
                "243",
                "58000",
                "57600",
                "1",
                "13000",
                "0.14",
                "0.13",
                "12"
            ),
            listOf(
                "Fiat",
                "Panda 4x4",
                "1996",
                "AUTOMATIC",
                "WHITE",
                "ICE",
                "3310",
                "1500",
                "4",
                "TRUE",
                "TRUE",
                "204",
                "FALSE",
                "50.8513",
                "5.6971",
                "OR-682-Z",
                "64",
                "7550",
                "6890",
                "15",
                "12630",
                "0.17",
                "0.12",
                "19"
            ),
            listOf(
                "Subaru",
                "Impreza STI",
                "2003",
                "MANUAL",
                "BLUE",
                "ICE",
                "4420",
                "1740",
                "4",
                "TRUE",
                "TRUE",
                "306",
                "FALSE",
                "51.4964",
                "3.6084",
                "OD-561-PX",
                "88",
                "10000",
                "8600",
                "12",
                "11250",
                "0.13",
                "0.15",
                "21"
            ),
            listOf(
                "Porsche",
                "911",
                "1970",
                "MANUAL",
                "YELLOW",
                "ICE",
                "4163",
                "1610",
                "2",
                "FALSE",
                "FALSE",
                "200",
                "FALSE",
                "51.4368",
                "5.4652",
                "OZ-12-LJ",
                "134",
                "69990",
                "62550",
                "36",
                "4000",
                "0.24",
                "0.21",
                "19"
            ),
            listOf(
                "Vanguard",
                "CitiCar",
                "1976",
                "AUTOMATIC",
                "ORANGE",
                "BEV",
                "2437",
                "1397",
                "2",
                "FALSE",
                "TRUE",
                "35",
                "FALSE",
                "52.3487",
                "6.6678",
                "IO-091-PU",
                "43",
                "9800",
                "6550",
                "32",
                "1200",
                "0.11",
                "0.13",
                "6"
            ),
            listOf(
                "Volkswagen",
                "Golf GTI 1600",
                "1980",
                "MANUAL",
                "BLACK",
                "ICE",
                "3820",
                "1610",
                "4",
                "TRUE",
                "TRUE",
                "370",
                "FALSE",
                "52.5103",
                "5.4645",
                "LK-946-E",
                "88",
                "16990",
                "16850",
                "19",
                "11000",
                "0.18",
                "0.12",
                "17"
            ),
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

    fun generateAvailabilitiesMock() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date

        fun atHour(date: LocalDate, hour: Int) =
            LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, 0)

        createdCars.forEach { car ->
            // First availability window: covers all reservations
            // From 60 days ago to 45 days in the future
            availabilitiesRepository.create(
                carId = car.id,
                startDate = atHour(today.plus(DatePeriod(days = -60)), 6),
                endDate = atHour(today.plus(DatePeriod(days = 45)), 20)
            )

            // Gap of 3-7 days (randomized)
            val gapDays = Random.nextInt(3, 8)
            val secondWindowStart = today.plus(DatePeriod(days = 45 + gapDays))

            // Second availability window: random duration (10-30 days)
            val secondWindowDuration = Random.nextInt(10, 31)
            val secondWindowEnd = secondWindowStart.plus(DatePeriod(days = secondWindowDuration))

            availabilitiesRepository.create(
                carId = car.id,
                startDate = atHour(secondWindowStart, 8),
                endDate = atHour(secondWindowEnd, 20)
            )
        }
    }

    fun generateReservationsMock() {
        val now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today: LocalDate = now.date

        fun atHour(date: LocalDate, hour: Int, minute: Int = 0) =
            LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, minute)

        // Helper function to create a reservation with random duration and time offset
        fun createReservationPeriod(
            startDate: LocalDate,
            minDuration: Int = 2,
            maxDuration: Int = 7,
            hourOffset: Int = 0
        ): Pair<LocalDateTime, LocalDateTime> {
            val duration = Random.nextInt(minDuration, maxDuration + 1)
            val startHour = 8 + Random.nextInt(-2 + hourOffset, 3 + hourOffset).coerceIn(8, 18)
            val startMinute = Random.nextInt(0, 4) * 15 // 0, 15, 30, or 45

            val start = atHour(startDate, startHour, startMinute)
            val endDate = startDate.plus(DatePeriod(days = duration))
            val endHour = 8 + Random.nextInt(-2, 3).coerceIn(8, 20)
            val endMinute = Random.nextInt(0, 4) * 15
            val end = atHour(endDate, endHour, endMinute)

            return Pair(start, end)
        }

        // Create different types of reservations for each car
        var renterIndex = 0

        createdCars.forEach { car ->
            // 1. COMPLETED: 2 reservations in the past
            run {
                val startDate1 = today.plus(DatePeriod(days = -20))
                val (start1, end1) = createReservationPeriod(startDate1, minDuration = 2, maxDuration = 5)

                val reservation1 = reservationRepo.createReservation(
                    userId = createdRenters[renterIndex].id,
                    carId = car.id,
                    startDate = start1,
                    endDate = end1
                )
                reservationRepo.updateStatus(reservation1.id, ReservationStatus.COMPLETED)
                createdReservations.add(reservation1.copy(status = ReservationStatus.COMPLETED))

                renterIndex = (renterIndex + 1) % createdRenters.count()

                val startDate2 = today.plus(DatePeriod(days = -12))
                val (start2, end2) = createReservationPeriod(startDate2, minDuration = 2, maxDuration = 4)

                val reservation2 = reservationRepo.createReservation(
                    userId = createdRenters[renterIndex].id,
                    carId = car.id,
                    startDate = start2,
                    endDate = end2
                )
                reservationRepo.updateStatus(reservation2.id, ReservationStatus.COMPLETED)
                createdReservations.add(reservation2.copy(status = ReservationStatus.COMPLETED))

                renterIndex = (renterIndex + 1) % createdRenters.count()
            }

            // 2. ACTIVE: 1 reservation that includes today
            run {
                val daysBeforeToday = Random.nextInt(1, 3)
                val startDate = today.plus(DatePeriod(days = -daysBeforeToday))
                val daysAfterToday = Random.nextInt(1, 4)
                val duration = daysBeforeToday + daysAfterToday

                val startHour = 8 + Random.nextInt(-2, 3).coerceIn(8, 18)
                val start = atHour(startDate, startHour, Random.nextInt(0, 4) * 15)

                val endDate = startDate.plus(DatePeriod(days = duration))
                val endHour = 8 + Random.nextInt(-2, 3).coerceIn(8, 20)
                val end = atHour(endDate, endHour, Random.nextInt(0, 4) * 15)

                val reservation = reservationRepo.createReservation(
                    userId = createdRenters[renterIndex].id,
                    carId = car.id,
                    startDate = start,
                    endDate = end
                )
                reservationRepo.updateStatus(reservation.id, ReservationStatus.ACTIVE)
                createdReservations.add(reservation.copy(status = ReservationStatus.ACTIVE))

                renterIndex = (renterIndex + 1) % createdRenters.count()
            }

            // 3. UPCOMING: 3 reservations in the future
            run {
                // Near future (3-7 days from now)
                val startDate1 = today.plus(DatePeriod(days = Random.nextInt(3, 8)))
                val (start1, end1) = createReservationPeriod(startDate1, minDuration = 2, maxDuration = 6)

                val reservation1 = reservationRepo.createReservation(
                    userId = createdRenters[renterIndex].id,
                    carId = car.id,
                    startDate = start1,
                    endDate = end1
                )
                createdReservations.add(reservation1)

                renterIndex = (renterIndex + 1) % createdRenters.count()

                // Mid future (10-20 days from now)
                val startDate2 = today.plus(DatePeriod(days = Random.nextInt(10, 21)))
                val (start2, end2) = createReservationPeriod(startDate2, minDuration = 3, maxDuration = 7)

                val reservation2 = reservationRepo.createReservation(
                    userId = createdRenters[renterIndex].id,
                    carId = car.id,
                    startDate = start2,
                    endDate = end2
                )
                createdReservations.add(reservation2)

                renterIndex = (renterIndex + 1) % createdRenters.count()

                // Far future (25-40 days from now)
                val startDate3 = today.plus(DatePeriod(days = Random.nextInt(25, 41)))
                val (start3, end3) = createReservationPeriod(startDate3, minDuration = 3, maxDuration = 8)

                val reservation3 = reservationRepo.createReservation(
                    userId = createdRenters[renterIndex].id,
                    carId = car.id,
                    startDate = start3,
                    endDate = end3
                )
                createdReservations.add(reservation3)

                renterIndex = (renterIndex + 1) % createdRenters.count()
            }
        }
    }

    fun generateRidesMock() {

        data class RideCoords(
            val startX: Float, val startY: Float,
            val endX: Float, val endY: Float
        )

        val routes = listOf(
            RideCoords(52.0905f, 5.1214f, 52.3720f, 4.8952f), // Utrecht → Amsterdam
            RideCoords(51.4416f, 5.4697f, 51.9853f, 5.9112f), // Eindhoven → Nijmegen
            RideCoords(53.2194f, 6.5665f, 52.0790f, 4.3130f)  // Groningen → The Hague
        )

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        // Only COMPLETED or ACTIVE reservations
        val eligibleReservations = createdReservations.filter {
            it.status == ReservationStatus.COMPLETED || it.status == ReservationStatus.ACTIVE
        }

        eligibleReservations.forEach { reservation ->

            val ridesCount = Random.nextInt(1, 4) // 1-3 rides per reservation

            repeat(ridesCount) {

                val route = routes.random()

                // Generate random ride start date in past 30 days
                val daysAgo = Random.nextInt(1, 31)
                val startHour = Random.nextInt(6, 22)
                val startMinute = Random.nextInt(0, 4) * 15 // 0,15,30,45
                val rideStart = now.date.minus(DatePeriod(days = daysAgo))
                    .let { date ->
                        LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, startHour, startMinute)
                    }

                // Ride duration 1-4 hours
                val durationHours = Random.nextInt(1, 5)
                val endHour = (startHour + durationHours).coerceAtMost(23)
                val endMinute = startMinute
                val rideEnd = LocalDateTime(rideStart.year, rideStart.monthNumber, rideStart.dayOfMonth, endHour, endMinute)

                // Random distance between 50 and 300 km
                val distanceKm = ((Random.nextDouble(50.0, 300.0) * 10).roundToInt()) / 10.0
                val lengthMeters = (distanceKm * 1000).toInt()
                val durationSeconds = durationHours * 3600

                val ride = ridesRepository.create(
                    startX = route.startX,
                    startY = route.startY,
                    endX = route.endX,
                    endY = route.endY,
                    length = lengthMeters,
                    duration = durationSeconds,
                    reservationId = reservation.id,
                    dateTimeStart = rideStart,
                    dateTimeEnd = rideEnd,
                    distanceTravelled = distanceKm
                )

                createdRides.add(ride)
            }
        }
    }

    fun generateBonusPointsMock() {
        createdRides.forEach { ride ->
            val bonusPoints = bonusPointsRepository.create(
                points = Random.nextInt(-2,6),
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