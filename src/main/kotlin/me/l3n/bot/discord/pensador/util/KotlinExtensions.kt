package me.l3n.bot.discord.pensador.util


fun Result.Companion.success() = success(Unit)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class NoArgConstructor
