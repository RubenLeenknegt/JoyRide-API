package leafcar.backend.dto.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import leafcar.backend.domain.User
import leafcar.backend.domain.UserType

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val emailAddress: String,
    val userType: UserType,
    val bankAccount: String? = null,
    val bankAccountName: String? = null,
    val vehicleLocation: String? = null,
    val password: String
)
