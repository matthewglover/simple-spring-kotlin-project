package com.matthewglover.simpleproject.features.users

import arrow.core.computations.either
import com.matthewglover.simpleproject.common.database.awaitFirstRow
import com.matthewglover.simpleproject.common.database.parseValue
import com.matthewglover.simpleproject.errors.ApplicationError
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val databaseClient: DatabaseClient) {

    suspend fun findUserById(userId: String) = either<ApplicationError, User> {
        val row = databaseClient
            .sql("SELECT email, age FROM users WHERE user_id = :userId")
            .bind("userId", userId.toInt())
            .awaitFirstRow()
            .bind()

        val email = row.parseValue<String>("email").bind()
        val age = row.parseValue<Int>("age").bind()

        RawUser(userId = userId, email = email, age = age).refine().bind()
    }

    suspend fun addUser(newUser: NewUser): User {
        val (email, age) = newUser

        return databaseClient
            .sql("INSERT INTO users(email, age) VALUES (:email, :age) RETURNING user_id")
            .bind("email", email.value)
            .bind("age", age.value)
            .map { row -> row.get("user_id", Integer::class.java)!!.toInt() }
            .one()
            .map { userId -> User(userId = userId.toString(), email = email, age = age) }
            .awaitFirst()
    }
}
