package me.l3n.bot.discord.pensador.service.handler

import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.on
import kotlin.reflect.KClass
import kotlin.reflect.cast


abstract class EventHandler<T : Event> {

    abstract val type: KClass<T>

    inline fun <reified T : Event> register(discord: Kord) {
        discord.on<T> {
            if (type.isInstance(this))
                handle(type.cast(this))
        }
    }

    abstract val handle: suspend T.() -> Unit
}
