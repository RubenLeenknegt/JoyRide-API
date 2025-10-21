package leafcar.backend.services

import com.zaxxer.hikari.HikariDataSource
import kotlinx.datetime.LocalDate
import leafcar.backend.EnvironmentSetup
import leafcar.backend.TestDatabaseConnection
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import leafcar.backend.domain.UserType
import leafcar.backend.repository.UserRepository
import org.jetbrains.exposed.sql.Database

val testdb = Database.connect(HikariDataSource(TestDatabaseConnection.getDataSource()))

class AuthServiceTest {
    private val authService = AuthService(UserRepository())
    val plainPassword = "ditiseenwachtwoord"
    val hashedPlainPassword = "\$2a\$10\$Ry5HkukzQEAqA2BcYre5I.lMjuVuFFEGCCj1OQB93B2IoDuSH1EGm\n"
    val setup = EnvironmentSetup.setup(testdb)
    @Test
    fun verifyPassword() {
        val user = authService.registration(
            emailAddress = "test123@hotlook.com",
            password = plainPassword,
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
            vehicleLocation = null
        )
        assertNotNull(authService.verifyPassword(user!!.emailAddress, plainPassword))
    }

    @Test
    fun createPasswordHash() {
    }

    @Test
    fun registration() {
    }

}