package joyride.backend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object TestDatabaseConnection {
    private val config = HikariConfig().apply {
        jdbcUrl = TestDatabaseConfig.dbUrl
        username = TestDatabaseConfig.dbUser
        password = TestDatabaseConfig.dbPass
        driverClassName = TestDatabaseConfig.dbDriver
        maximumPoolSize = 5
        isAutoCommit = true
    }

    private val ds: HikariDataSource by lazy {
        var attempts = 0
        while (attempts < 5) {
            try {
                return@lazy HikariDataSource(config)
            } catch (e: Exception) {
                attempts++
                println("DB connectie mislukt, poging $attempts, wacht 3s...")
                Thread.sleep(10000)
            }
        }
        throw RuntimeException("Kan geen connectie maken met DB na 5 pogingen")
    }

    fun getDataSource(): HikariDataSource = ds
}
