package me.l3n.bot.discord.pensador.config.converter

import dev.kord.common.entity.Snowflake
import org.eclipse.microprofile.config.spi.Converter

object SnowflakeConverter : Converter<Snowflake> {
    override fun convert(value: String): Snowflake {
        return Snowflake(value.toLong())
    }
}
