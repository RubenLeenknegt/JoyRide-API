package leafcar.backend.dao

import leafcar.backend.domain.UserType
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

/**
     * Represents the database table for storing user information.
     * This table is defined using Kotlin Exposed's `IdTable` for managing
     * entities with a primary key.
     */
    object UsersTable : IdTable<String>("Users") {

        /**
         * The primary key column for the table.
         * Stores a unique identifier for each user as a 36-character string.
         */
        override val id = varchar("id", 36).entityId()

        /**
         * Column for storing the user's first name.
         * Maximum length: 100 characters.
         */
        val firstName = varchar("first_name", 100)

        /**
         * Column for storing the user's last name.
         * Maximum length: 100 characters.
         */
        val lastName = varchar("last_name", 100)

        /**
         * Column for storing the user's birth date.
         * Uses the `date` type from Kotlin Exposed.
         */
        val birthDate = date("birth_date")

        /**
         * Column for storing the user's email address.
         * Maximum length: 100 characters.
         */
        val emailAddress = varchar("email_address", 100)

        /**
         * Column for storing the user's password hash.
         * Stored as a binary value with a maximum size of 64 bytes.
         */
        val passwordHash = varchar("password_hash", 255)

        /**
         * Column for storing the user's type.
         * Uses an enumeration mapped by name with a maximum length of 50 characters.
         */
        val userType = enumerationByName("user_type", 50, UserType::class)
    }