package me.l3n.bot.discord.pensador.service.handler

import dev.kord.core.entity.Message
import dev.kord.core.entity.User

abstract class CommandHandler {

    abstract val name: String

    abstract suspend fun handle(args: List<String>, message: Message, user: User): Result<Unit>
}
