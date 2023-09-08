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
