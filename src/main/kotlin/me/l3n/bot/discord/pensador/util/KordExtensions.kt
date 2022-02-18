package me.l3n.bot.discord.pensador.util

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.channel.TextChannel

suspend fun Kord.getTextChannel(value: Snowflake): TextChannel =
    getChannelOf(value)
        ?: throw IllegalArgumentException("Channel ID $value not found")

infix fun Kord.isNotMe(user: UserBehavior) = this.selfId != user.id
