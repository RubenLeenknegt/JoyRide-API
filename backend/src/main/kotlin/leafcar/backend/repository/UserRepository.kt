package leafcar.backend.repository

import leafcar.backend.domain.User
import org.example.leafcar.backend.dao.UserEntity
import org.example.leafcar.backend.dao.toDomain
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun getAll(): List<User> = transaction {
        UserEntity.all().map { it.toDomain() }
    }
}