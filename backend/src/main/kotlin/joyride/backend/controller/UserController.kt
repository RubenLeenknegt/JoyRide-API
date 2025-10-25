package joyride.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import joyride.backend.dto.request.AccountModifyRequest
import joyride.backend.repository.UserUpdateResult
import joyride.backend.repository.UserRepository
import joyride.backend.services.JwtConfig.dotenv

/**
 * Defines the routes for managing user-related operations in the application.
 *
 * @param userRepository The repository used to interact with user data.
 */
fun Route.userRouting(userRepository: UserRepository) {
    // Authenticate routes using the JWT backend authentication name from environment variables
    authenticate(dotenv["JWT_BACKEND_AUTH_NAME"]) {
        route("/users") {

            /**
             * GET endpoint to retrieve all users.
             * Responds with a list of all users in the system.
             */
            get {
                val users = userRepository.getAll()
                call.respond(status = HttpStatusCode.OK, users)
            }

            /**
             * PUT endpoint to modify a specific attribute of a user account.
             * Accepts the user ID, the key of the attribute to modify, and the new value.
             *
             * Responds with the updated user object or an error if the operation fails.
             */
            put("/account/{userId}/{key}") {
                // Extract the user ID and key from the route parameters
                val userId = call.parameters["userId"].toString()
                val key = call.parameters["key"].toString()
                val value = call.receive<AccountModifyRequest>().value
                val output = userRepository.updateVariables(key, value, userId)

                // Handle the result of the update operation
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

            /**
             * DELETE endpoint to delete a user account.
             * Accepts the user ID and verifies it against the token's ID claim.
             *
             * Responds with a success message or an error if the operation fails.
             */
            delete("/account/{userId}") {
                val userId = call.parameters["userId"].toString()
                val jwtPrincipal = call.principal<JWTPrincipal>()
                val idClaim: String? = jwtPrincipal?.getClaim("id", String::class)
                if (userId != idClaim) {
                    return@delete call.respond(HttpStatusCode.Unauthorized, "Token id and userId do not match")
                } else {
                    try {
                        userRepository.deleteUser(userId)
                        call.respond(HttpStatusCode.OK, "User with id $userId was deleted")
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            "There was an error processing your request for user id $userId"
                        )
                    }
                }
            }
        }
    }
}