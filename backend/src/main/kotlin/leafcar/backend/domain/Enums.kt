package leafcar.backend.domain

import kotlinx.serialization.Serializable

/**
 * Enum representing the types of vehicle transmissions.
 * This enum is used to specify the transmission type of a vehicle.
 */
@Serializable
enum class TransmissionType {
    MANUAL,
    AUTOMATIC,
    SEMI_AUTOMATIC
}

/**
 * Enum representing the colors of a vehicle.
 * This enum is used to specify the color of a vehicle.
 */
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

/**
 * Enum representing the types of fuel used by a vehicle.
 * This enum is used to specify the fuel type of a vehicle.
 */
@Serializable
enum class FuelType {
    ICE, // Internal Combustion Engine
    BEV, // Battery Electric Vehicle
    FCEV // Fuel Cell Electric Vehicle
}

/**
 * Enum representing the types of users in the system.
 * This enum is used to specify whether a user is an owner or a renter.
 */
@Serializable
enum class UserType {
    OWNER,
    RENTER
}