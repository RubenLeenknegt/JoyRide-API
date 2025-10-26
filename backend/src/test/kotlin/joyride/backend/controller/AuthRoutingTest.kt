package joyride.backend.controller

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.LocalDate
import joyride.backend.EnvironmentSetup
import joyride.backend.Testmodule
import joyride.backend.api.auth.LoginRequest
import joyride.backend.domain.User
import joyride.backend.domain.UserType
import joyride.backend.dto.request.RegisterRequest
import joyride.backend.dto.response.LoginResponse
import joyride.backend.repository.UserRepository
import joyride.backend.services.AuthService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs


class AuthRoutingTest {
    private val authService = AuthService(UserRepository())

    @BeforeEach
    fun setUp() {
        EnvironmentSetup.setup()
    }

    @Test
    fun loginAssertResponseSuccess() = testApplication {
        application {
            Testmodule()
        }
        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val plainPassword = "SomeRandomPassword"
        val user = authService.registration(
            emailAddress = "test123@hotlook.com",
            password = plainPassword,
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )!!
        val response = jsonClient.post("/auth/users/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(user.emailAddress, plainPassword))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val loginResponse = response.body<LoginResponse>()
        assertIs<LoginResponse>(loginResponse)
        assertEquals(user.emailAddress, loginResponse.user.emailAddress)

    }

    @Test
    fun loginAssertResponseUnauthorized() = testApplication {
        application {
            Testmodule()
        }
        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val plainPassword = "SomeRandomPassword"
        val wrongPlainPassword = "WrongPassword"
        val user = authService.registration(
            emailAddress = "test123@hotlook.com",
            password = plainPassword,
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )!!
        val response = jsonClient.post("/auth/users/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(user.emailAddress, wrongPlainPassword))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val error = response.body<Map<String, String>>()
        assertEquals("Invalid credentials", error.getValue("error"))
    }


    @Test
    fun registerResponseSuccess() = testApplication {
        application {
            Testmodule()
        }
        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val plainPassword = "SomeRandomPassword"
        val user = User(
            id = UUID.randomUUID().toString(),
            emailAddress = "test123@hotlook.com",
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val response = jsonClient.post("/auth/users/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    user.firstName,
                    user.lastName,
                    user.birthDate,
                    user.emailAddress,
                    user.userType,
                    user.bankAccount,
                    user.bankAccountName,
                    plainPassword
                )
            )
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertIs<LoginResponse>(response.body<LoginResponse>())
        val userOnDb = UserRepository().findCredentialsByEmail(user.emailAddress)!!
        assertIs<User>(authService.verifyPassword(user.emailAddress, plainPassword))
        assertEquals(user.emailAddress, userOnDb.user.emailAddress)
        assertEquals(response.body<LoginResponse>().user.id, userOnDb.user.id)
        assertEquals(user.firstName, userOnDb.user.firstName)
        assertEquals(user.lastName, userOnDb.user.lastName)
        assertEquals(user.birthDate, userOnDb.user.birthDate)
        assertEquals(user.userType, userOnDb.user.userType)
        assertEquals(user.bankAccount, userOnDb.user.bankAccount)
        assertEquals(user.bankAccountName, userOnDb.user.bankAccountName)
    }

    @Test
    fun registerResponseConflict() = testApplication {
        application {
            Testmodule()
        }
        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val plainPassword = "SomeRandomPassword"
        val user = User(
            id = UUID.randomUUID().toString(),
            emailAddress = "test123@hotlook.com",
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val response = jsonClient.post("/auth/users/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    user.firstName,
                    user.lastName,
                    user.birthDate,
                    user.emailAddress,
                    user.userType,
                    user.bankAccount,
                    user.bankAccountName,
                    plainPassword
                )
            )
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertIs<LoginResponse>(response.body<LoginResponse>())
        val userOnDb = UserRepository().findCredentialsByEmail(user.emailAddress)!!
        assertIs<User>(authService.verifyPassword(user.emailAddress, plainPassword))
        assertEquals(user.emailAddress, userOnDb.user.emailAddress)
        assertEquals(response.body<LoginResponse>().user.id, userOnDb.user.id)
        assertEquals(user.firstName, userOnDb.user.firstName)
        assertEquals(user.lastName, userOnDb.user.lastName)
        assertEquals(user.birthDate, userOnDb.user.birthDate)
        assertEquals(user.userType, userOnDb.user.userType)
        assertEquals(user.bankAccount, userOnDb.user.bankAccount)
        assertEquals(user.bankAccountName, userOnDb.user.bankAccountName)

        val response2 = jsonClient.post("/auth/users/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    user.firstName,
                    user.lastName,
                    user.birthDate,
                    user.emailAddress,
                    user.userType,
                    user.bankAccount,
                    user.bankAccountName,
                    plainPassword
                )
            )
        }
        assertEquals(HttpStatusCode.Conflict, response2.status)
        val message = response2.body<Map<String, String>>()
        assertEquals("Email already registered", message.getValue("error"))

    }

    @Test
    fun refreshTokenSuccess() = testApplication {
        application {
            Testmodule()
        }
        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }
        val plainPassword = "SomeRandomPassword"
        val user = User(
            id = UUID.randomUUID().toString(),
            emailAddress = "test123@hotlook.com",
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val response = jsonClient.post("/auth/users/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    user.firstName,
                    user.lastName,
                    user.birthDate,
                    user.emailAddress,
                    user.userType,
                    user.bankAccount,
                    user.bankAccountName,
                    plainPassword
                )
            )
        }
        val response2 = jsonClient.post("/refresh")
        assertEquals(HttpStatusCode.OK, response2.status)

    }
    @Test
    fun refreshTokenAssertUnauthorised() = testApplication {
        application {
            Testmodule()
        }
        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val plainPassword = "SomeRandomPassword"
        val user = User(
            id = UUID.randomUUID().toString(),
            emailAddress = "test123@hotlook.com",
            firstName = "Peter",
            lastName = "Janssen",
            birthDate = LocalDate.parse("1998-05-29"),
            userType = UserType.RENTER,
            bankAccount = null,
            bankAccountName = null,
        )
        val response = jsonClient.post("/auth/users/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    user.firstName,
                    user.lastName,
                    user.birthDate,
                    user.emailAddress,
                    user.userType,
                    user.bankAccount,
                    user.bankAccountName,
                    plainPassword
                )
            )
        }

        val response2 = jsonClient.post("/refresh")
        assertEquals(HttpStatusCode.Unauthorized, response2.status)

    }
}
