package me.l3n.bot.discord.pensador.service.handler

import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.on
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class EventHandler<T : Event>(private val type: KClass<T>) {

    fun register(discord: Kord) {
        discord.on<Event> {
            if (type.isInstance(this))
                handler(type.cast(this))
        }
    }

    abstract val handler: suspend T.() -> Unit
}
