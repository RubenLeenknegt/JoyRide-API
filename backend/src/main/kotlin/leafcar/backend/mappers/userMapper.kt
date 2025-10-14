package leafcar.backend.mappers

import leafcar.backend.dao.UserEntity
import leafcar.backend.domain.User
import leafcar.backend.dto.UserDto

object userMapper {

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
        userType = this.userType
    )

    fun UserEntity.toUserDto(): UserDto = UserDto(
        id = this.id.value,
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = this.birthDate,
        emailAddress = this.emailAddress,
        userType = this.userType
    )

    fun User.toDto(): UserDto =
        UserDto(
            id = id,
            emailAddress = emailAddress,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            userType = userType,
        )
}