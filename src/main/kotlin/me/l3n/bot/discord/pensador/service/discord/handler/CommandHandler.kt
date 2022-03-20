package me.l3n.bot.discord.pensador.service.discord.handler

import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.DmChannel

abstract class CommandHandler {

    abstract val name: String

    abstract suspend fun handle(args: List<String>, context: CommandContext): Result<Unit>
}

data class CommandContext(
    val message: Message,
    val user: User,
    val channel: DmChannel
)
