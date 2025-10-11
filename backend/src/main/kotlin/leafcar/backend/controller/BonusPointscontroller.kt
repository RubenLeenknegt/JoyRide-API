package leafcar.backend.controller

import io.ktor.http.HttpStatusCode
import leafcar.backend.repository.BonusPointsRepository
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import leafcar.backend.dao.BonusPointsEntity
import leafcar.backend.domain.BonusPoints

fun Route.bonusPointsRouting(bonusPointsRepository: BonusPointsRepository) {
    route("/bonuspoints"){
        get {
            val bonuspoints = bonusPointsRepository.getAll()
            call.respond(status = HttpStatusCode.OK, bonuspoints)
        }
        post {
            try {
                val req = call.receive<BonusPoints>()
                // Adjust to your repository API as needed
                val created = bonusPointsRepository.create(
                    userId = req.userId,
                    rideId = req.rideId,
                    points = req.points
                )
                call.respond(HttpStatusCode.Created, created)
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing JSON body.")
            }
        }
    }
}