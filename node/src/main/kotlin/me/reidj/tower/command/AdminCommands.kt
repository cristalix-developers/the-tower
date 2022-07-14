package me.reidj.tower.command

import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.user.User
import ru.cristalix.core.formatting.Formatting

object AdminCommands {

    private val gods = setOf(
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd", // reidj
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd", // func
    )

    init {
        command("money") { player, args -> app.getUser(player)?.giveMoney(args[0].toInt()) }

        command("tokens") { player, args -> app.getUser(player)?.giveTokens(args[0].toInt()) }

        command("exp") { player, args -> app.getUser(player)?.giveExperience(args[0].toInt()) }

        adminConsume("rebirth") { user, args -> user?.giveRebirth(args[0].toInt()) }
    }

    private fun adminConsume(name: String, consumer: (user: User?, args: Array<out String>) -> Unit) {
        command(name) { player, args ->
            if (player.isOp || gods.contains(player.uniqueId.toString())) {
                consumer(app.getUser(player), args)
                Formatting.fine("Успешно.")
            } else {
                Formatting.error("Нет прав.")
            }
        }
    }
}