package leafcar.backend.services

import kotlinx.datetime.LocalDate
import leafcar.backend.EnvironmentSetup
import leafcar.backend.domain.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import leafcar.backend.domain.UserType
import leafcar.backend.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


class AuthServiceTest {
    private val authService = AuthService(UserRepository())
    @BeforeEach
    fun setUp() {
        EnvironmentSetup.setup()
    }

    @Test
    fun verifyPassword() {
        val invalidEmail = "thisemaildoesnotexist@gotmail.yes"
        val plainPassword = "ditiseenwachtwoord"
        val wrongPassword = "ditIseenFoutWacthwoord"
        val user = authService.registration(
            emailAddress = "test123@hotlook.com",
            password = plainPassword,
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        assertInstanceOf(User::class.java, user)
        assertNotNull(authService.verifyPassword(user!!.emailAddress, plainPassword))
        assertNull(authService.verifyPassword(invalidEmail, plainPassword))
        assertNull(authService.verifyPassword(user.emailAddress, wrongPassword))


    }

    @Test
    fun createPasswordHash() {
        val plainPassword = "ditiseenwachtwoord"
        val hash = authService.createPasswordHash(plainPassword)
        assertNotEquals(plainPassword, hash)
        assertTrue(BCryptPasswordEncoder().matches(plainPassword, hash))
    }

    @Test
    fun registration() {
        val plainPassword = "ditiseenwachtwoord"
        val emailAddress = "test123@hotlook.com"
        val password = plainPassword
        val firstName = "Peter"
        val lastName = "Janssen"
        val birthDate = LocalDate.parse("1998-05-29")
        val userType = UserType.RENTER
        val bankAccount = null
        val bankAccountName = null

        val user = authService.registration(
            emailAddress = emailAddress,
            password = password,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            userType = userType,
            bankAccount = bankAccount,
            bankAccountName = bankAccountName,
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
        }
        val user2 = authService.registration(
            emailAddress = emailAddress,
            password = password,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            userType = userType,
            bankAccount = bankAccount,
            bankAccountName = bankAccountName,
        )
        assertNull(user2)
    }

}