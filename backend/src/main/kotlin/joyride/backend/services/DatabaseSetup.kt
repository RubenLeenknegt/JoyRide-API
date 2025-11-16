//package joyride.backend.services
//
//import org.jetbrains.exposed.sql.SchemaUtils
//
//object DatabaseSetup {
//    transaction(database) {
//        SchemaUtils.create(
//            Users,
//            CarsTable,
//            Reservations,
//            AvailabilitiesTable,
//            Rides,
//            Photos,
//            BonusPointsTable
//        )
//    }
//}