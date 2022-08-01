package me.reidj.tower.command

import me.func.mod.util.command
import me.reidj.tower.coroutine
import me.reidj.tower.user.User
import me.reidj.tower.withUser
import ru.cristalix.core.formatting.Formatting

object AdminCommands {

    private val gods = setOf(
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd", // reidj
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd", // func
    )

    init {
        command("money") { player, args -> coroutine { withUser(player) { giveMoney(args[0].toInt()) } } }

        command("tokens") { player, args -> coroutine { withUser(player) { giveTokens(args[0].toInt()) } } }

        command("exp") { player, args -> coroutine { withUser(player) { giveExperience(args[0].toInt()) } } }

        adminConsume("rebirth") { user, args -> user.giveRebirth(args[0].toInt()) }
    }

    private fun adminConsume(name: String, consumer: (user: User, args: Array<out String>) -> Unit) {
        command(name) { player, args ->
            if (player.isOp || gods.contains(player.uniqueId.toString())) {
                coroutine { withUser(player) { consumer(this, args) } }
                player.sendMessage(Formatting.fine("Успешно."))
            } else {
                player.sendMessage(Formatting.error("Нет прав."))
            }
        }
    }
}