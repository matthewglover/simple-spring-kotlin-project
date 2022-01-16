package com.matthewglover.simpleproject.features.users

import com.matthewglover.simpleproject.utils.SimpleRows
import com.matthewglover.simpleproject.utils.TestDatabaseConfig
import com.matthewglover.simpleproject.utils.executeSqlScript
import com.matthewglover.simpleproject.utils.fetchAllRows
import com.matthewglover.simpleproject.utils.getOrThrow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.r2dbc.core.DatabaseClient

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest : TestDatabaseConfig() {

    @Autowired
    lateinit var databaseClient: DatabaseClient

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("classpath:/db/user_repository_test_data.sql")
    lateinit var userRepositoryTestData: Resource

    lateinit var testUsers: SimpleRows

    @BeforeAll
    fun setup() = runBlocking {
        databaseClient.executeSqlScript(userRepositoryTestData)

        testUsers = databaseClient.sql("select * from users").fetchAllRows()
    }

    @Test
    internal fun `findUserById - returns Right of user when valid userId`() = runBlocking {
        val testUserId = testUsers.findRowBy("email", "matt@home.com")!!["user_id"].toString()

        val expected = User(userId = testUserId, email = Email("matt@home.com"), age = UserAge(45))

        val actual = userRepository.findUserById(testUserId).getOrThrow()

        assertEquals(expected, actual)
    }
}
