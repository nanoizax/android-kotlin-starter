package com.sonholab.androidstarter.features.users.data.repository

import app.cash.turbine.test
import com.sonholab.androidstarter.features.users.data.local.UserDao
import com.sonholab.androidstarter.features.users.data.local.UserEntity
import com.sonholab.androidstarter.features.users.data.remote.UsersApi
import com.sonholab.androidstarter.features.users.data.remote.dto.UserDto
import com.sonholab.androidstarter.features.users.domain.model.UserSummary
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UsersRepositoryImplTest {

    private lateinit var usersApi: UsersApi
    private lateinit var userDao: UserDao
    private lateinit var repository: UsersRepositoryImpl

    // -------------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------------

    private val fakeEntities = listOf(
        UserEntity(
            id = 1,
            name = "Alice Smith",
            email = "alice@example.com",
            username = "alice",
            phone = "+1-555-1234",
            website = "alice.dev",
            companyName = "Acme Corp",
        ),
        UserEntity(
            id = 2,
            name = "Bob Jones",
            email = "bob@example.com",
            username = "bob",
            phone = "+1-555-5678",
            website = "bob.io",
            companyName = "Globex",
        ),
    )

    private val fakeDtos = listOf(
        UserDto(
            id = 1,
            name = "Alice Smith",
            email = "alice@example.com",
            username = "alice",
            phone = "+1-555-1234",
            website = "alice.dev",
            company = UserDto.CompanyDto(name = "Acme Corp", catchPhrase = null, bs = null),
        ),
        UserDto(
            id = 2,
            name = "Bob Jones",
            email = "bob@example.com",
            username = "bob",
            phone = "+1-555-5678",
            website = "bob.io",
            company = UserDto.CompanyDto(name = "Globex", catchPhrase = null, bs = null),
        ),
    )

    @Before
    fun setUp() {
        usersApi = mockk()
        userDao = mockk(relaxUnitFun = true)
        repository = UsersRepositoryImpl(usersApi, userDao)
    }

    // -------------------------------------------------------------------------
    // observeUsers
    // -------------------------------------------------------------------------

    @Test
    fun `observeUsers emits mapped domain models from DAO`() = runTest {
        every { userDao.observeAll() } returns flowOf(fakeEntities)

        repository.observeUsers().test {
            val emitted = awaitItem()
            assertEquals(2, emitted.size)
            assertEquals(
                UserSummary(
                    id = 1,
                    name = "Alice Smith",
                    email = "alice@example.com",
                    username = "alice",
                    companyName = "Acme Corp",
                ),
                emitted[0],
            )
            assertEquals(
                UserSummary(
                    id = 2,
                    name = "Bob Jones",
                    email = "bob@example.com",
                    username = "bob",
                    companyName = "Globex",
                ),
                emitted[1],
            )
            awaitComplete()
        }
    }

    @Test
    fun `observeUsers emits empty list when DAO returns empty list`() = runTest {
        every { userDao.observeAll() } returns flowOf(emptyList())

        repository.observeUsers().test {
            val emitted = awaitItem()
            assertEquals(emptyList<UserSummary>(), emitted)
            awaitComplete()
        }
    }

    @Test
    fun `observeUsers emits multiple updates as DAO flow emits`() = runTest {
        val firstBatch = listOf(fakeEntities[0])
        val secondBatch = fakeEntities

        every { userDao.observeAll() } returns flowOf(firstBatch, secondBatch)

        repository.observeUsers().test {
            val first = awaitItem()
            assertEquals(1, first.size)
            assertEquals("Alice Smith", first[0].name)

            val second = awaitItem()
            assertEquals(2, second.size)

            awaitComplete()
        }
    }

    // -------------------------------------------------------------------------
    // refresh
    // -------------------------------------------------------------------------

    @Test
    fun `refresh fetches from API and replaces local cache`() = runTest {
        coEvery { usersApi.getUsers() } returns fakeDtos

        repository.refresh()

        coVerify(exactly = 1) { usersApi.getUsers() }
        coVerify(exactly = 1) { userDao.deleteAll() }
        coVerify(exactly = 1) { userDao.insertAll(any()) }
    }

    @Test
    fun `refresh maps API response to correct entities before inserting`() = runTest {
        coEvery { usersApi.getUsers() } returns fakeDtos
        val capturedEntities = slot<List<UserEntity>>()
        coEvery { userDao.insertAll(capture(capturedEntities)) } returns Unit

        repository.refresh()

        val inserted = capturedEntities.captured
        assertEquals(2, inserted.size)
        assertEquals(1, inserted[0].id)
        assertEquals("Alice Smith", inserted[0].name)
        assertEquals("alice@example.com", inserted[0].email)
        assertEquals("Acme Corp", inserted[0].companyName)
    }

    @Test
    fun `refresh deletes before inserting to ensure consistency`() = runTest {
        coEvery { usersApi.getUsers() } returns fakeDtos
        val callOrder = mutableListOf<String>()
        coEvery { userDao.deleteAll() } answers { callOrder.add("delete") }
        coEvery { userDao.insertAll(any()) } answers { callOrder.add("insert") }

        repository.refresh()

        assertEquals(listOf("delete", "insert"), callOrder)
    }

    @Test(expected = Exception::class)
    fun `refresh propagates API exceptions`() = runTest {
        coEvery { usersApi.getUsers() } throws Exception("Network failure")

        repository.refresh()
    }
}
