package joyride.backend.utils

import io.ktor.server.application.*
import io.ktor.server.plugins.origin

fun ApplicationCall.baseUrl(): String {
    val origin = request.origin
    return "${origin.scheme}://${origin.serverHost}"
}
