package org.example.leafcar.backend.dao

    import leafcar.backend.dao.UsersTable
    import leafcar.backend.domain.User
    import org.jetbrains.exposed.dao.Entity

    import org.jetbrains.exposed.dao.EntityClass
    import org.jetbrains.exposed.dao.id.EntityID

/**
     * Represents the User entity in the database.
     * This class maps the `UsersTable` database table to a Kotlin object
     * and provides access to the table's columns via delegated properties.
     */
    class UserEntity(id: EntityID<String>) : Entity<String>(id) {
        /**
         * Companion object that acts as a DAO (Data Access Object) for the `UserEntity`.
         * Provides methods for querying and interacting with the `UsersTable`.
         */
        companion object : EntityClass<String, UserEntity>(UsersTable)

        // Maps to the `first_name` column in the `UsersTable`.
        val firstName by UsersTable.firstName

        // Maps to the `last_name` column in the `UsersTable`.
        val lastName by UsersTable.lastName

        // Maps to the `birth_date` column in the `UsersTable`.
        val birthDate by UsersTable.birthDate

        // Maps to the `email_address` column in the `UsersTable`.
        val emailAdress by UsersTable.emailAdress

        // Maps to the `user_type` column in the `UsersTable`.
        val userType by UsersTable.userType

        // Maps to the `password_hash` column in the `UsersTable`.
        // Stores the hashed password for the user.
        val passwordHash by UsersTable.passwordHash
    }

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
        emailAddress = this.emailAdress,
        userType = this.userType,
    )