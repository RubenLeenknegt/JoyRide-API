package leafcar.backend.controller


import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import leafcar.backend.dto.request.AccountModifyRequest
import leafcar.backend.mappers.UserMapper.toDomain
import leafcar.backend.mappers.UserMapper.toUserType
import leafcar.backend.repository.UserRepository
import leafcar.backend.services.AuthService
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.userRouting(userRepository: UserRepository) {
    route("/users") {
        get {
            val users = userRepository.getAll()
            call.respond(status = HttpStatusCode.OK, users)
        }
        put("/account/{userId}/{key}") {
            val userId = call.parameters["userId"].toString()
            val key = call.parameters["key"].toString()
            val req = call.receive<AccountModifyRequest>()
            val allowedVariables = listOf(
                "firstName",
                "lastName",
                "emailAddress",
                "password",
                "userType",
                "bankAccount",
                "bankAccountName",
                "vehicleLocation"
            )
            if (key !in allowedVariables) {
                return@put call.respond(status = HttpStatusCode.Forbidden, "Not allowed to edit atribute")
            }
//            TODO: if userId != authentication user claim
            val userEntity = transaction { userRepository.findById(userId) } ?: return@put call.respond(
                status = HttpStatusCode.NotFound,
                "The user was not found"
            )

            when (key) {
                "firstName" -> transaction {userEntity.firstName = req.value }
                "lastName" -> transaction {userEntity.lastName = req.value}
                "emailAddress" -> transaction { userEntity.emailAddress = req.value }
                "password" -> transaction {userEntity.passwordHash = AuthService(userRepository).createPasswordHash(req.value)} // consider hashing
                "userType" -> transaction { userEntity.userType = toUserType(req.value) }
                "bankAccount" -> transaction { userEntity.bankAccount = req.value }
                "bankAccountName" -> transaction { userEntity.bankAccountName = req.value }
                "vehicleLocation" -> transaction { userEntity.vehicleLocation = req.value }
                else -> return@put call.respond(HttpStatusCode.BadRequest, "Unknown key")
            }
            call.respond(status = HttpStatusCode.OK, userEntity.toDomain())


        }

    }
}
