package leafcar.backend.controller


import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import leafcar.backend.dto.request.AccountModifyRequest
import leafcar.backend.repository.UserUpdateResult
import leafcar.backend.repository.UserRepository
import leafcar.backend.services.JwtConfig.dotenv


fun Route.userRouting(userRepository: UserRepository) {
    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {
        route("/users") {
            get {
                val users = userRepository.getAll()
                call.respond(status = HttpStatusCode.OK, users)
            }
            put("/account/{userId}/{key}") {
//            TODO: if userId != authentication user claim

                val userId = call.parameters["userId"].toString()
                val key = call.parameters["key"].toString()
                val value = call.receive<AccountModifyRequest>().value
                val output = userRepository.updateVariables(key, value, userId)

                when (output) {
                    is UserUpdateResult.Error -> when (output.message) {
                        "Not allowed to edit attribute" -> return@put call.respond(
                            status = HttpStatusCode.Forbidden,
                            "Not allowed to edit atribute"
                        )

                        "The user was not found" -> return@put call.respond(
                            status = HttpStatusCode.NotFound,
                            "The user was not found"
                        )

                        "Unknown key" -> return@put call.respond(HttpStatusCode.BadRequest, "Unknown key")
                    }

                    is UserUpdateResult.Success -> call.respond(HttpStatusCode.OK, output.user)
                }

            }
            delete("/account/{userId}") {
                val userId = call.parameters["userId"].toString()
                val jwtPrincipal = call.principal<JWTPrincipal>()
                val idClaim: String? = jwtPrincipal?.getClaim("id", String::class)
                if (userId != idClaim) {
                    return@delete call.respond(HttpStatusCode.Unauthorized, "Token id and userId do not match")
                }
                else {
                    userRepository.deleteUser(userId)
                    call.respond(HttpStatusCode.OK, "User with $userId was deleted")
                }
            }
        }
    }
}
