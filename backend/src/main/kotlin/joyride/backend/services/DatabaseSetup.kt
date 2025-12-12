package joyride.backend.services

import org.jetbrains.exposed.sql.SchemaUtils
import joyride.backend.dao.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSetup {
    fun createTables(database: Database) {
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