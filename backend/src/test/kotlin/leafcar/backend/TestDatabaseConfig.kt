package leafcar.backend

object TestDatabaseConfig {
    //    val dbUrl: String = "jdbc:mysql://localhost:3306/UnitTestDb"
//    val dbUser: String = "root"
//    val dbPass: String = "root"
    const val dbUrl: String = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL"
    const val dbDriver: String = "org.h2.Driver"
    const val dbUser: String = "sa"
    const val dbPass: String = ""
}