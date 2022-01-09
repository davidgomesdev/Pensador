package me.l3n.bot.discord.pensador.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import java.io.File
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces


@ApplicationScoped
class ConfigLoader {

    @Produces
    fun loadConfig(): Config = ConfigLoader.Builder()
        .addFileSource(File("config/application.yaml"), true)
        .addDefaultSources()
        .build()
        .loadConfigOrThrow()
}
