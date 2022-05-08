package me.l3n.bot.discord.pensador.service.discord.util

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.channel.TextChannel

suspend fun Kord.getTextChannel(value: Snowflake): TextChannel =
    getChannelOf(value)
        ?: throw IllegalArgumentException("Channel ID $value not found")

infix fun Kord.isNotSelf(user: UserBehavior) = this.selfId != user.id
