package joyride.backend.services

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import joyride.backend.domain.UserType
import joyride.backend.repository.CarRepository
import joyride.backend.repository.ReservationRepository
import joyride.backend.repository.UserRepository

val userRepository = UserRepository()
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
            userRepository.createUser(
                emailAddress = emailAddress,
                passwordHash = passwordHashed,
                firstName = firstName,
                lastName = lastName,
                birthDate = LocalDate.parse(birthDate),
                userType = UserType.valueOf(userTypeStr),
            )
        }
    }
}

fun generateReservationsMock() {
    val reservationRepo = ReservationRepository()
    val userRepo = UserRepository()
    val carRepo = CarRepository()

    val renters = userRepo.getAll().filter { it.userType == UserType.RENTER }
    if (renters.isEmpty()) return

    // Use car IDs from init.sql as a reference, only include those that exist in the DB
    val referenceCarIds = listOf(
        "4b285f64-5717-4562-b3fc-2c963f66b001",
        "4b285f64-5717-4562-b3fc-2c963f66b002",
        "4b285f64-5717-4562-b3fc-2c963f66b003",
        "4b285f64-5717-4562-b3fc-2c963f66b004",
        "4b285f64-5717-4562-b3fc-2c963f66b005",
        "4b285f64-5717-4562-b3fc-2c963f66b006",
        "4b285f64-5717-4562-b3fc-2c963f66b007",
        "4b285f64-5717-4562-b3fc-2c963f66b008",
        "4b285f64-5717-4562-b3fc-2c963f66b009",
        "4b285f64-5717-4562-b3fc-2c963f66b010"
    )

    val availableCarIds = referenceCarIds.filter { carRepo.getById(it).isNotEmpty() }
    if (availableCarIds.isEmpty()) return

    val now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val today: LocalDate = now.date

    fun createIfNotExists(userId: String, carId: String, start: LocalDateTime, end: LocalDateTime) {
        reservationRepo.createReservation(userId, carId, start, end)
    }

    // Helper to choose user/car deterministically
    fun pickUser(index: Int) = renters[index % renters.size].id
    fun pickCar(index: Int) = availableCarIds[index % availableCarIds.size]

    fun atHour(date: LocalDate, hour: Int) = LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, 0)

    // Create 2 reservations more than 5 days before now
    run {
        val d1 = today.plus(DatePeriod(days = -6))
        val start1 = atHour(d1, 9)
        val end1 = atHour(d1, 15)
        createIfNotExists(pickUser(0), pickCar(0), start1, end1)

        val d2 = today.plus(DatePeriod(days = -8))
        val start2 = atHour(d2, 10)
        val end2 = atHour(d2, 14)
        createIfNotExists(pickUser(1), pickCar(1), start2, end2)
    }

    // Create 2 reservations within +/- 5 days of now (one in the past few days, one upcoming)
    run {
        val d1 = today.plus(DatePeriod(days = -2))
        val start1 = atHour(d1, 9)
        val end1 = atHour(d1, 14)
        createIfNotExists(pickUser(2), pickCar(2), start1, end1)

        val d2 = today.plus(DatePeriod(days = 3))
        val start2 = atHour(d2, 11)
        val end2 = atHour(d2, 18)
        createIfNotExists(pickUser(3), pickCar(3), start2, end2)
    }

    // Create 2 reservations more than 5 days after now
    run {
        val d1 = today.plus(DatePeriod(days = 6))
        val start1 = atHour(d1, 8)
        val end1 = atHour(d1, 16)
        createIfNotExists(pickUser(4), pickCar(4), start1, end1)

        val d2 = today.plus(DatePeriod(days = 10))
        val start2 = atHour(d2, 9)
        val end2 = atHour(d2, 15)
        createIfNotExists(pickUser(5), pickCar(5), start2, end2)
    }
}
