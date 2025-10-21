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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


class AuthServiceTest {
    private val testdb = Database.connect(HikariDataSource(TestDatabaseConnection.getDataSource()))
    private val authService = AuthService(UserRepository())
    val plainPassword = "ditiseenwachtwoord"
    val setup = EnvironmentSetup.setup(testdb)

    @Test
    fun verifyPassword() {
        setup
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
        setup
        val hash = authService.createPasswordHash(plainPassword)
        assertNotEquals(plainPassword, hash)
        assertTrue(BCryptPasswordEncoder().matches(plainPassword, hash))
    }

    @Test
    fun registration() {
        setup
        val emailAddress = "test123@hotlook.com"
        val password = plainPassword
        val firstName = "Peter"
        val lastName = "Janssen"
        val birthDate = LocalDate.parse("1998-05-29")
        val userType = UserType.RENTER
        val bankAccount = null
        val bankAccountName = null
        val vehicleLocation = null

        val user = authService.registration(
            emailAddress = emailAddress,
            password = password,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            userType = userType,
            bankAccount = bankAccount,
            bankAccountName = bankAccountName,
            vehicleLocation = vehicleLocation
        )
        assertNotNull(user)
        if (user != null) {
            assertEquals(emailAddress, user.emailAddress)
            assertEquals(firstName, user.firstName)
            assertEquals(lastName, user.lastName)
            assertEquals(birthDate, user.birthDate)
            assertEquals(userType, user.userType)
            assertEquals(bankAccount, user.bankAccount)
            assertEquals(bankAccountName, user.bankAccountName)
            assertEquals(vehicleLocation, user.vehicleLocation)
        }
    }

}