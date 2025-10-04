package org.example.leafcar.backend.dao

import leafcar.backend.domain.User


class UserEntity {

}

fun UserEntity.toDomain(): User = User(
    id = this.id.value,
    firstName = this.firstName,
    lastName = this.lastName,
    birthDate = this.birthDate.,
    emailAddress = this.emailAdress,
    userType = this.userType,
)