package me.l3n.bot.discord.pensador.service.discord.handler

import dev.kord.core.Kord

interface EventHandler {

    fun register(discord: Kord)
}
