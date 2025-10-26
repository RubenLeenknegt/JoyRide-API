package joyride.backend

object TestDatabaseConfig {
    const val dbUrl: String = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL"
    const val dbDriver: String = "org.h2.Driver"
    const val dbUser: String = "sa"
    const val dbPass: String = ""
}