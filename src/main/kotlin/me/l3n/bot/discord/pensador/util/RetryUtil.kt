package me.l3n.bot.discord.pensador.util


inline fun <T> retryUntil(
    block: () -> T,
    isValid: (T) -> Boolean,
    beforeRetry: () -> Unit = {},
    afterRetry: () -> Unit = {},
): T {
    var value = block()

    while (!isValid(value)) {
        beforeRetry()
        value = block()
        afterRetry()
    }

    return value
}

/**
 * @param afterRetry is called only if [block] fails
 */
inline fun <T> retryTimes(
    times: Int,
    block: () -> Result<T>,
    beforeRetry: (Int) -> Unit = {},
    afterRetry: (Throwable) -> Unit = {},
    retryExceeded: () -> Unit = {},
): Result<T> {
    for (i in 0 until times) {
        if (i != 0) beforeRetry(i)

        val result = block()

        if (result.isSuccess) return result
        afterRetry(result.exceptionOrNull()!!)
    }

    val result = block()

    if (result.isFailure) {
        afterRetry(result.exceptionOrNull()!!)
        retryExceeded()
    }

    return result
}
