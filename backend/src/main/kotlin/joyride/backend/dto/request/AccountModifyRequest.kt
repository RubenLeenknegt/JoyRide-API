package joyride.backend.dto.request

import kotlinx.serialization.Serializable

/**
 * Request DTO for modifying a single account attribute.
 *
 * @property value New value to apply to the account property.
 */

@Serializable
data class AccountModifyRequest(
    val value: String
)
