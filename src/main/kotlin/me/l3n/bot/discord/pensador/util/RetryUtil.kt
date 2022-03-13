package me.l3n.bot.discord.pensador.util

// Inspired by `Flow.retry`
inline fun <T> retryUntil(
    block: () -> T,
    isValid: (T) -> Boolean,
    beforeRetry: () -> Unit = {},
    afterRetry: (T) -> Unit = {},
): T {
    var value = block()

    while (!isValid(value)) {
        beforeRetry()
        value = block()
        afterRetry(value)
    }

    return value
}

inline fun <T> retry(
    times: Int,
    block: () -> Result<T>,
    beforeRetry: (Int) -> Unit = {},
    afterRetry: (Throwable) -> Unit = {},
    retryExceeded: (Int) -> Unit = {},
): Result<T> {
    var value = block()

    if (value.isSuccess) return value

    repeat(times) { i ->
        beforeRetry(i + 1)
        value = block()

        if (value.isFailure)
            afterRetry(value.exceptionOrNull()!!)
    }

    retryExceeded(times)

    return value
}
