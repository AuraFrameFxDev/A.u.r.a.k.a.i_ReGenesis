package dev.aurakai.auraframefx.domains.genesis.network

/**
 * A sealed class that encapsulates successful response with data
 * or an error state with a message.
 *
 * @param T The type of data expected in the response
 */
sealed class NetworkResponse<out T> {
    /**
     * Represents a successful network response with [data].
     */
    data class Success<T>(val data: T) : NetworkResponse<T>() {
        override fun toString(): String = "[Success: data=$data]"
    }

    /**
     * Represents a network error with an optional [message] and [cause].
     */
    data class Error(
        val message: String? = null,
        val cause: Throwable? = null,
        val code: Int? = null
    ) : NetworkResponse<Nothing>() {
        constructor(throwable: Throwable) : this(
            message = throwable.message,
            cause = throwable.cause,
            code = (throwable as? io.ktor.client.plugins.ClientRequestException)?.response?.status?.value
        )

        override fun toString(): String = "[Error: message=$message, code=$code]"
    }

    /**
     * Represents a loading state.
     */
    object Loading : NetworkResponse<Nothing>() {
        override fun toString(): String = "[Loading]"
    }

    /**
     * Returns the data if this is a [Success] result, or null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Returns the data if this is a [Success] result, or throws an exception otherwise.
     *
     * @throws IllegalStateException if the result is not a [Success]
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw IllegalStateException("No data available: $message", cause)
        Loading -> throw IllegalStateException("No data available: Loading")
    }

    /**
     * Executes [action] if this is a [Success] result.
     */
    inline fun onSuccess(action: (T) -> Unit): NetworkResponse<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes [action] if this is an [Error] result.
     */
    inline fun onError(action: (Error) -> Unit): NetworkResponse<T> {
        if (this is Error) action(this)
        return this
    }

    /**
     * Executes [action] if this is a [Loading] result.
     */
    inline fun onLoading(action: () -> Unit): NetworkResponse<T> {
        if (this is Loading) action()
        return this
    }
}

/**
 * Wraps a suspending API [call] in try-catch, returning a [NetworkResponse].
 *
 * @param T The type of data expected in the response
 * @param call The suspending function to execute
 * @return [NetworkResponse] containing the result
 */
suspend fun <T> safeApiCall(
    call: suspend () -> T
): NetworkResponse<T> = try {
    NetworkResponse.Success(call())
} catch (e: Exception) {
    NetworkResponse.Error(e)
}

/**
 * Extension function to map a [Result] to a [NetworkResponse].
 */
fun <T> Result<T>.toNetworkResponse(): NetworkResponse<T> = fold(
    onSuccess = { NetworkResponse.Success(it) },
    onFailure = { NetworkResponse.Error(it) }
)

/**
 * Extension function to transform the data of a [NetworkResponse] if it's a [NetworkResponse.Success].
 */
inline fun <T, R> NetworkResponse<T>.map(transform: (T) -> R): NetworkResponse<R> = when (this) {
    is NetworkResponse.Success -> NetworkResponse.Success(transform(data))
    is NetworkResponse.Error -> this
    NetworkResponse.Loading -> NetworkResponse.Loading
}

/**
 * Extension function to transform the error of a [NetworkResponse] if it's a [NetworkResponse.Error].
 */
inline fun <T> NetworkResponse<T>.mapError(transform: (NetworkResponse.Error) -> NetworkResponse.Error): NetworkResponse<T> =
    when (this) {
        is NetworkResponse.Success -> this
        is NetworkResponse.Error -> transform(this)
        NetworkResponse.Loading -> NetworkResponse.Loading
    }

