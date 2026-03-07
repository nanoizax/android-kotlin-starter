package com.sonholab.androidstarter.features.auth.domain.usecase

import com.sonholab.androidstarter.core.domain.Result
import com.sonholab.androidstarter.features.auth.domain.model.User
import com.sonholab.androidstarter.features.auth.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var loginUseCase: LoginUseCase

    private val validEmail = "john@example.com"
    private val validPassword = "secret123"
    private val fakeUser = User(
        id = "1",
        email = validEmail,
        name = "John Doe",
        avatarUrl = null,
        role = "user",
    )

    @Before
    fun setUp() {
        authRepository = mockk()
        loginUseCase = LoginUseCase(authRepository)
    }

    // -------------------------------------------------------------------------
    // Validation tests
    // -------------------------------------------------------------------------

    @Test
    fun `returns Error when email is blank`() = runTest {
        val result = loginUseCase(email = "", password = validPassword)

        assertTrue("Expected Result.Error for blank email", result is Result.Error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `returns Error when email has no at sign`() = runTest {
        val result = loginUseCase(email = "invalidemail", password = validPassword)

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `returns Error when email has no domain after at sign`() = runTest {
        val result = loginUseCase(email = "user@", password = validPassword)

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `returns Error when password is too short`() = runTest {
        val result = loginUseCase(email = validEmail, password = "abc")

        assertTrue("Expected Result.Error for short password", result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is IllegalArgumentException)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `returns Error when password is exactly 5 characters`() = runTest {
        val result = loginUseCase(email = validEmail, password = "12345")

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `passes through with valid credentials of minimum length`() = runTest {
        coEvery { authRepository.login(validEmail, "123456") } returns Result.Success(fakeUser)

        val result = loginUseCase(email = validEmail, password = "123456")

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { authRepository.login(validEmail, "123456") }
    }

    // -------------------------------------------------------------------------
    // Repository delegation tests
    // -------------------------------------------------------------------------

    @Test
    fun `delegates to repository with trimmed credentials`() = runTest {
        val emailWithSpaces = "  $validEmail  "
        coEvery { authRepository.login(validEmail, validPassword) } returns Result.Success(fakeUser)

        loginUseCase(email = emailWithSpaces, password = validPassword)

        coVerify(exactly = 1) { authRepository.login(validEmail, validPassword) }
    }

    @Test
    fun `returns Success with User when repository succeeds`() = runTest {
        coEvery { authRepository.login(validEmail, validPassword) } returns Result.Success(fakeUser)

        val result = loginUseCase(email = validEmail, password = validPassword)

        assertTrue(result is Result.Success)
        assertEquals(fakeUser, (result as Result.Success).data)
    }

    @Test
    fun `returns Error when repository returns Error`() = runTest {
        val networkException = Exception("Network error")
        coEvery { authRepository.login(any(), any()) } returns Result.Error(networkException)

        val result = loginUseCase(email = validEmail, password = validPassword)

        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertNotNull(error.exception)
        assertEquals("Network error", error.exception.message)
    }

    @Test
    fun `calls repository exactly once per invocation`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.Success(fakeUser)

        loginUseCase(email = validEmail, password = validPassword)
        loginUseCase(email = validEmail, password = validPassword)

        coVerify(exactly = 2) { authRepository.login(validEmail, validPassword) }
    }
}
