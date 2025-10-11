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

fun Route.userRouting(userRepository: UserRepository) {
    val auth = Authentication(userRepository)

    route("/users") {
        get {
            val users = userRepository.getAll()
            call.respond(status = HttpStatusCode.OK, users)
        }
    }
    route("/auth/user") {
        post {
            val request = call.receive<LoginRequest>()
            val user = auth.verifyPassword(email = request.email, password = request.password)
            if (user != null) {
                call.respond(HttpStatusCode.OK, LoginResponse(user))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }
        get {  }
    }
}