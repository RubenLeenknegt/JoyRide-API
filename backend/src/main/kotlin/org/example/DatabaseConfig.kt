package org.example

object DatabaseConfig {
    val dbUrl: String = "jdbc:mysql://db:3306/${System.getenv("MYSQL_DATABASE") ?: "local_db"}"
    val dbUser: String = System.getenv("MYSQL_USER") ?: "local_user"
    val dbPass: String = System.getenv("MYSQL_PASSWORD") ?: "local_pass"
}