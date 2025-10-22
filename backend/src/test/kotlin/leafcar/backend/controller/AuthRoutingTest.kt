package leafcar.backend.controller

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.*
import io.ktor.client.statement.request
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.LocalDate
import leafcar.backend.EnvironmentSetup
import leafcar.backend.Testmodule
import leafcar.backend.api.auth.LoginRequest
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.dto.request.RegisterRequest
import leafcar.backend.dto.response.LoginResponse
import leafcar.backend.repository.UserRepository
import leafcar.backend.services.AuthService
import org.junit.jupiter.api.Assertions.*
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
            vehicleLocation = null
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
            vehicleLocation = null
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
            vehicleLocation = null
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
                    user.vehicleLocation,
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
        assertEquals(user.vehicleLocation, userOnDb.user.vehicleLocation)
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
            vehicleLocation = null
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
                    user.vehicleLocation,
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
        assertEquals(user.vehicleLocation, userOnDb.user.vehicleLocation)

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
                    user.vehicleLocation,
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
            vehicleLocation = null
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
                    user.vehicleLocation,
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
            vehicleLocation = null
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
                    user.vehicleLocation,
                    plainPassword
                )
            )
        }

        val response2 = jsonClient.post("/refresh")
        assertEquals(HttpStatusCode.Unauthorized, response2.status)

    }
}
