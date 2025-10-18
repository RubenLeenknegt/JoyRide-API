package leafcar.backend.repository

import leafcar.backend.domain.User

sealed class UserUpdateResult {
    data class Success(val user: User) : UserUpdateResult()
    data class Error(val message: String) : UserUpdateResult()
}