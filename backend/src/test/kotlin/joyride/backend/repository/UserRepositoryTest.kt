package joyride.backend.repository

import kotlinx.datetime.LocalDate
import joyride.backend.EnvironmentSetup
import joyride.backend.domain.User
import joyride.backend.domain.UserType
import joyride.backend.services.AuthService
import joyride.backend.services.userRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertIs

class UserRepositoryTest {
    private val authService = AuthService(UserRepository())

    @BeforeEach
    fun setup() {
        EnvironmentSetup.setup()
    }

    @Test
    fun getAllTest2UsersEqualsListNotNull() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val user2 = userRepository.createUser(
            emailAddress = "test123456@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter2",
            lastName = "Janssen2",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.OWNER,
        )

        val users = userRepository.getAll()
        assertEquals(user1, users[0])
        assertEquals(user2, users[1])
    }

    @Test
    fun createUserAssertFindByEmailEquals() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        assertEquals(user1, userRepository.findByEmail(user1.emailAddress))
    }

    @Test
    fun findByEmail() {
    }

    @Test
    fun findCredentialsByEmail() {
    }

    @Test
    fun updateVariablesEmailEqualsSuccess() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val key = "emailAddress"
        val newValue = "someemail@someprovider.nl"
        val updatedUser = userRepository.updateVariables(key, newValue, user1.id)
        assertIs<UserUpdateResult.Success>(updatedUser)
        assertEquals(newValue, updatedUser.user.emailAddress)
    }


    @Test
    fun updateVariablesFirstNameEqualsSuccess() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val newFirstName = "Voornaam"
        val updatedUser = userRepository.updateVariables("firstName", newFirstName, user1.id)
        assertIs<UserUpdateResult.Success>(updatedUser)
        assertEquals(newFirstName, updatedUser.user.firstName)
    }

    @Test
    fun updateVariablesLastNameEqualsSuccess() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val newLastName = "achternaam"
        val updatedUser = userRepository.updateVariables("lastName", newLastName, user1.id)
        assertIs<UserUpdateResult.Success>(updatedUser)
        assertEquals(newLastName, updatedUser.user.lastName)

    }

    @Test
    fun updateVariablesUserTypeEqualsSuccess() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val key = "userType"
        val newValue = UserType.OWNER
        val updatedUser = userRepository.updateVariables(key, newValue.toString(), user1.id)
        assertIs<UserUpdateResult.Success>(updatedUser)
        assertEquals(newValue, updatedUser.user.userType)
    }

    @Test
    fun updateVariablesBankAccountEqualsSuccess() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val key = "bankAccount"
        val newValue = "NLBA238405739"
        val updatedUser = userRepository.updateVariables(key, newValue, user1.id)
        assertIs<UserUpdateResult.Success>(updatedUser)
        assertEquals(newValue, updatedUser.user.bankAccount)
    }

    @Test
    fun updateVariablesBankAccountNameEqualsSuccess() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val key = "bankAccountName"
        val newValue = "DHR P Janssen"
        val updatedUser = userRepository.updateVariables(key, newValue, user1.id)
        assertIs<UserUpdateResult.Success>(updatedUser)
        assertEquals(newValue, updatedUser.user.bankAccountName)
    }


    @Test
    fun updateVariablesPasswordEqualsSuccess() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )

        val key = "password"
        val newValue = "someNewPassword"
        val updatedUser = userRepository.updateVariables(key, newValue, user1.id)
        assertIs<UserUpdateResult.Success>(updatedUser)
        assertIs<User>(authService.verifyPassword(user1.emailAddress, newValue))
    }

    @Test
    fun updateVariablesWrongKeyFalse() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )

        val key = "birthDate"
        val newValue = "2000-02-30"
        val updatedUser = userRepository.updateVariables(key, newValue, user1.id)
        assertIs<UserUpdateResult.Error>(updatedUser)
    }

    @Test
    fun deleteUser() {
        val user1 = userRepository.createUser(
            emailAddress = "test123@hotlook.com",
            passwordHash = authService.createPasswordHash("ditiseenwachtwoord"),
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        assertNotNull(userRepository.findByEmail(user1.emailAddress))
        assertTrue { userRepository.deleteUser(user1.id) }
        assertNull(userRepository.findByEmail(user1.emailAddress))
    }

    @Test
    fun deleteUserNoId() {
        assertFalse { userRepository.deleteUser("randomnonexistentuuid") }

    }
}