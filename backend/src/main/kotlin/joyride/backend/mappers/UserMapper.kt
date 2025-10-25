package joyride.backend.mappers

import joyride.backend.dao.UserEntity
import joyride.backend.domain.User
import joyride.backend.domain.UserType
import joyride.backend.dto.UserDto

/**
 * Object responsible for mapping between different user-related data models.
 *
 * This includes converting between `UserEntity`, `User`, and `UserDto` objects,
 * as well as utility functions for handling user types.
 */
object UserMapper {

    /**
     * Extension function to convert a `UserEntity` to a `User` domain object.
     * This function excludes sensitive fields like `passwordHash` from the domain object.
     *
     * @receiver The `UserEntity` instance to convert.
     * @return A `User` object representing the domain model.
     */
    fun UserEntity.toDomain(): User = User(
        id = this.id.value, // Converts the EntityID to its raw value (String).
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = this.birthDate,
        emailAddress = this.emailAddress,
        userType = this.userType,
        bankAccount = this.bankAccount,
        bankAccountName = this.bankAccountName,
    )

    /**
     * Converts a `UserEntity` to a `UserDto` object.
     *
     * @receiver The `UserEntity` instance to convert.
     * @return A `UserDto` object representing the data transfer model.
     */
    fun UserEntity.toUserDto(): UserDto = UserDto(
        id = this.id.value,
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = this.birthDate,
        emailAddress = this.emailAddress,
        userType = this.userType,
        bankAccount = this.bankAccount,
        bankAccountName = this.bankAccountName,
    )

    /**
     * Converts a `User` domain object to a `UserDto` object.
     *
     * @receiver The `User` instance to convert.
     * @return A `UserDto` object representing the data transfer model.
     */
    fun User.toDto(): UserDto =
        UserDto(
            id = this.id,
            emailAddress = this.emailAddress,
            firstName = this.firstName,
            lastName = this.lastName,
            birthDate = this.birthDate,
            userType = this.userType,
            bankAccount = this.bankAccount,
            bankAccountName = this.bankAccountName,
        )

    /**
     * Converts a string input to a `UserType` enum value.
     *
     * @param input The string representation of the user type.
     * @return The corresponding `UserType` enum value.
     * @throws IllegalArgumentException if the input does not match any `UserType`.
     */
    fun toUserType(input: String): UserType =
        UserType.values().firstOrNull { it.name.equals(input, ignoreCase = true) }
            ?: throw IllegalArgumentException("Unknown UserType: $input")
}