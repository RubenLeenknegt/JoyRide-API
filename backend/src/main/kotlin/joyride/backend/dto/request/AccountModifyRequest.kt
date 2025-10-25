package joyride.backend.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class AccountModifyRequest(
    val value: String
)
