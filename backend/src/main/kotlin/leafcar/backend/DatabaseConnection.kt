package leafcar.backend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

/**
 * Provides a HikariCP connection pool for database access.
 *
 * Configures the pool with parameters from [DatabaseConfig] and
 * retries up to 5 times if the initial connection fails.
 */

object DatabaseConnection {
    private val config = HikariConfig().apply {
        jdbcUrl = DatabaseConfig.dbUrl
        username = DatabaseConfig.dbUser
        password = DatabaseConfig.dbPass
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
                println("DB connectie mislukt, poging $attempts, wacht 10s...")
                Thread.sleep(10000)
            }
        }
        throw RuntimeException("Kan geen connectie maken met DB na 5 pogingen")
    }

//    fun getConnection(): Connection = ds.connection
    fun getDataSource(): HikariDataSource = ds
}
