package me.l3n.bot.discord.pensador.config.converter

import com.sksamuel.hoplite.*
import com.sksamuel.hoplite.decoder.NullHandlingDecoder
import com.sksamuel.hoplite.fp.Validated
import com.sksamuel.hoplite.fp.invalid
import dev.kord.common.entity.Snowflake
import org.eclipse.microprofile.config.spi.Converter
import kotlin.reflect.KType

object SnowflakeConverter : Converter<Snowflake> {
    override fun convert(value: String): Snowflake = Snowflake(value.toLong())
}

class SnowflakeDecoder : NullHandlingDecoder<Snowflake> {

    override fun safeDecode(node: Node, type: KType, context: DecoderContext): ConfigResult<Snowflake> =
        when (node) {
            is LongNode -> Validated.Valid(Snowflake(node.value))
            is StringNode -> Validated.Valid(Snowflake(node.value))
            else -> ConfigFailure.DecodeError(node, type).invalid()
        }

    override fun supports(type: KType): Boolean = type.classifier in listOf(Long::class, String::class)
}
