package leafcar.backend.controller

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.LocalDate
import leafcar.backend.EnvironmentSetup
import leafcar.backend.Testmodule
import leafcar.backend.api.auth.LoginRequest
import leafcar.backend.domain.UserType
import leafcar.backend.repository.UserRepository
import leafcar.backend.services.AuthService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AuthRoutingTest {
    private val authService = AuthService(UserRepository())

    @BeforeEach
    fun setUp() {
        EnvironmentSetup.setup()
    }

    @Test
    fun authroutingRegistrationAssertResponseSuccess() = testApplication {
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
//        assertIs(LoginResponse, response.body())
    }


//    @Test
//    fun authRouting() {
//    }

}
