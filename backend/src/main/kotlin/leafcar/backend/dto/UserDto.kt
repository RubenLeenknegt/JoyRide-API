package leafcar.backend.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import leafcar.backend.domain.UserType

@Serializable
data class UserDto(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val emailAddress: String,
    val userType: UserType,
    val bankAccount: String? = null,
    val bankAccountName: String? = null,
    val vehicleLocation: String? = null
)
