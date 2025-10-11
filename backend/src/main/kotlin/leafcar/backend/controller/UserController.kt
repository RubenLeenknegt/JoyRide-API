package leafcar.backend.controller


import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import leafcar.backend.api.auth.LoginRequest
import leafcar.backend.api.auth.LoginResponse
import leafcar.backend.repository.UserRepository
import leafcar.backend.service.Authentication
import leafcar.leafcar.backend.api.auth.RegisterRequest

fun Route.userRouting(userRepository: UserRepository) {
    val auth = Authentication(userRepository)
    route("/users") {
        get {
            val users = userRepository.getAll()
            call.respond(status = HttpStatusCode.OK, users)
        }
    }
    post("/users/login") {
        val request = call.receive<LoginRequest>()
        val user = auth.verifyPassword(email = request.email, password = request.password)
        if (user != null) {
            call.respond(HttpStatusCode.OK, LoginResponse(user))
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
    }
    post("/users/register") {
        val request = call.receive<RegisterRequest>()
        val created = auth.registration(
            emailAddress = request.user.emailAddress,
            password = request.password,
            firstName = request.user.firstName,
            lastName = request.user.lastName,
            birthDate = request.user.birthDate,
            userType = request.user.userType
        )
        if (created != null) {
            call.respond(HttpStatusCode.Created, LoginResponse(created))
        } else {
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already registered"))
        }
    }

}
