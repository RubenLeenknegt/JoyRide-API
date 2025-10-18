package leafcar.backend.domain

import kotlinx.serialization.Serializable

@Serializable
enum class TransmissionType {
    MANUAL,
    AUTOMATIC,
    SEMI_AUTOMATIC
}

@Serializable
enum class Color {
    GREEN,
    GREY,
    BEIGE,
    RED,
    BLUE,
    BLACK,
    WHITE,
    OTHER
}

@Serializable
enum class FuelType {
    ICE,
    BEV,
    FCEV
}

@Serializable
enum class UserType {
    OWNER,
    RENTER
}