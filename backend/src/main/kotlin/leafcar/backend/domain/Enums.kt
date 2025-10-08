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
    PETROL,
    DIESEL,
    ELECTRIC,
    HYBRID
}
/**
 * Enumeration of possible user roles within the system.
 *
 * Marked with `@Serializable` so values can be (de)serialized in API payloads.
 */
@Serializable
enum class UserType {
    /** Owner of a vehicle or account. */
    OWNER,

    /** Driver of a vehicle. */
    RENTER,

    /** Administrator with elevated privileges. */
    ADMIN
}