package joyride.backend.dto.request

import joyride.backend.domain.ReservationStatus
import kotlinx.serialization.Serializable

@Serializable
data class UpdateReservationStatusRequest(
    val status: ReservationStatus
)