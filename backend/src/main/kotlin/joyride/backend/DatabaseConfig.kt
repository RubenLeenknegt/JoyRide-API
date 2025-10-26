package joyride.backend

/**
 * Configuration object for database connection parameters.
 * Reads MySQL URL, user, and password from environment variables,
 * falling back to local defaults if not set.
 */

object DatabaseConfig {
    val dbUrl: String = "jdbc:mysql://db:3306/${System.getenv("MYSQL_DATABASE") ?: "local_db"}"
    val dbUser: String = System.getenv("MYSQL_USER") ?: "local_user"
    val dbPass: String = System.getenv("MYSQL_PASSWORD") ?: "local_pass"
}