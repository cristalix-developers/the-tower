package me.reidj.tower.command

import me.func.mod.util.command
import me.reidj.tower.user.User
import ru.cristalix.core.formatting.Formatting
import ru.kdev.simulatorapi.listener.SessionListener

object AdminCommands {

    private val gods = setOf(
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd", // reidj
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd", // func
    )

    init {
        adminConsume("money") { user, args -> user.giveMoney(args[0].toInt()) }

        adminConsume("tokens") { user, args -> user.giveTokens(args[0].toInt()) }
    }

    private fun adminConsume(name: String, consumer: (user: User, args: Array<out String>) -> Unit) {
        command(name) { player, args ->
            if (player.isOp || gods.contains(player.uniqueId.toString())) {
                consumer(SessionListener.simulator.getUser(player.uniqueId)!!, args)
                Formatting.fine("Успешно.")
            } else {
                Formatting.error("Нет прав.")
            }
        }
    }
}