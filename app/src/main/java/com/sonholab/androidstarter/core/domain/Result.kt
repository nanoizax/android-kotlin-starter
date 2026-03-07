package com.sonholab.androidstarter.core.domain

/**
 * A discriminated union that encapsulates a successful outcome with a value of type [T],
 * a failure with an arbitrary [Throwable] exception, or a loading state.
 */
sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val exception: Throwable) : Result<Nothing>()

    data object Loading : Result<Nothing>()

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    val dataOrNull: T? get() = (this as? Success)?.data
    val errorOrNull: Throwable? get() = (this as? Error)?.exception
}

// -------------------------------------------------------------------------
// Extension functions
// -------------------------------------------------------------------------

/**
 * Transforms a [Result.Success] value using [transform].
 * [Result.Error] and [Result.Loading] pass through unchanged.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error   -> this
    is Result.Loading -> Result.Loading
}

/**
 * Calls [onSuccess] if this is [Result.Success].
 * Returns [this] unchanged to allow chaining.
 */
inline fun <T> Result<T>.onSuccess(onSuccess: (T) -> Unit): Result<T> {
    if (this is Result.Success) onSuccess(data)
    return this
}

/**
 * Calls [onError] if this is [Result.Error].
 * Returns [this] unchanged to allow chaining.
 */
inline fun <T> Result<T>.onError(onError: (Throwable) -> Unit): Result<T> {
    if (this is Result.Error) onError(exception)
    return this
}

/**
 * Calls [onLoading] if this is [Result.Loading].
 * Returns [this] unchanged to allow chaining.
 */
inline fun <T> Result<T>.onLoading(onLoading: () -> Unit): Result<T> {
    if (this is Result.Loading) onLoading()
    return this
}

/**
 * Extracts the value from a [Result.Success] or throws the encapsulated exception
 * from a [Result.Error]. Throws [IllegalStateException] when called on [Result.Loading].
 */
fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error   -> throw exception
    is Result.Loading -> throw IllegalStateException("Result is still loading")
}

/**
 * Returns the value from a [Result.Success] or [defaultValue] for any other state.
 */
fun <T> Result<T>.getOrDefault(defaultValue: T): T =
    if (this is Result.Success) data else defaultValue

/**
 * Pattern-matches all three states and returns the value produced by the matching branch.
 */
inline fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (Throwable) -> R,
    onLoading: () -> R,
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Error   -> onError(exception)
    is Result.Loading -> onLoading()
}

/**
 * Wraps the result of [block] into a [Result], catching any [Exception] into [Result.Error].
 */
inline fun <T> runCatchingResult(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}
