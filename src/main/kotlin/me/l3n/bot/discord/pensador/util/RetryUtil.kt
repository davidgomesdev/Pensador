package me.l3n.bot.discord.pensador.util


/**
 * @param afterRetry is called only if [block] fails
 */
inline fun <T> retry(
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

    if (result.isFailure) retryExceeded()
    return result
}

/**
 * @param afterRetry is called only if [block] fails
 */
suspend inline fun <T> coRetry(
    times: Int,
    block: () -> Result<T>,
    noinline beforeRetry: suspend (Int) -> Unit = {},
    noinline afterRetry: suspend (Throwable) -> Unit = {},
    noinline retryExceeded: suspend () -> Unit = {},
): Result<T> {
    for (i in 0 until times) {
        if (i != 0) beforeRetry(i)

        val result = block()

        if (result.isSuccess) return result
        afterRetry(result.exceptionOrNull()!!)
    }

    val result = block()

    if (result.isFailure) retryExceeded()
    return result
}
