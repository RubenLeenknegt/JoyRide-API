package leafcar.backend.repository

import leafcar.backend.domain.User

/**
 * Represents the result of a user update operation.
 *
 * This sealed class is used to encapsulate the outcome of updating a user,
 * which can either be a success or an error.
 */
sealed class UserUpdateResult {

    /**
     * Represents a successful user update operation.
     *
     * @property user The updated `User` object.
     */
    data class Success(val user: User) : UserUpdateResult()

    /**
     * Represents an error that occurred during a user update operation.
     *
     * @property message A descriptive error message.
     */
    data class Error(val message: String) : UserUpdateResult()
}