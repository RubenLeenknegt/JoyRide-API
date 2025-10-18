package leafcar.backend.mappers

import leafcar.backend.dao.UserEntity
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType
import leafcar.backend.dto.UserDto

object UserMapper {

    /**
     * Extension function to convert a `UserEntity` to a `User` domain object.
     * This function excludes sensitive fields like `passwordHash` from the domain object.
     *
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
        vehicleLocation = this.vehicleLocation
    )

    fun UserEntity.toUserDto(): UserDto = UserDto(
        id = this.id.value,
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = this.birthDate,
        emailAddress = this.emailAddress,
        userType = this.userType,
        bankAccount = this.bankAccount,
        bankAccountName = this.bankAccountName,
        vehicleLocation = this.vehicleLocation
    )

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
            vehicleLocation = this.vehicleLocation
        )

    fun toUserType(input: String): UserType =
        UserType.values().firstOrNull { it.name.equals(input, ignoreCase = true) }
            ?: throw IllegalArgumentException("Unknown UserType: $input")
}