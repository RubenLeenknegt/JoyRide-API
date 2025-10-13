package leafcar.backend.controller


import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

import leafcar.backend.repository.UserRepository
import leafcar.backend.service.Auth


fun Route.userRouting(userRepository: UserRepository) {
    route("/users") {
        get {
            val users = userRepository.getAll()
            call.respond(status = HttpStatusCode.OK, users)
        }
    }
}
