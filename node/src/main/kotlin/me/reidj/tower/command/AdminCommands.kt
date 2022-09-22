package me.reidj.tower.command

import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.rank.RankManager
import me.reidj.tower.user.User
import me.reidj.tower.util.godSet
import org.bukkit.Bukkit
import ru.cristalix.core.formatting.Formatting

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class AdminCommands {

    init {
        regAdminCommand("token") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveToken(args[1].toDouble())
        }
        regAdminCommand("money") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveMoney(args[1].toDouble())
        }
        regAdminCommand("gem") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveGem(args[1].toInt())
        }
        regAdminCommand("exp") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveExperience(args[1].toDouble())
        }
        regAdminCommand("rank") { _, args ->
            val user = app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand
            val stat = user.stat
            val rank = stat.rank
            stat.rank = if (args[1].toBoolean()) rank.upgradeRank() ?: return@regAdminCommand else rank.downgradeRank()
                ?: return@regAdminCommand
            RankManager.createRank(user)
        }
        regAdminCommand("donate") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).stat.donates.add(args[1])
        }
    }

    private fun regAdminCommand(commandName: String, executor: (User, Array<out String>) -> Unit) {
        command(commandName) { player, args ->
            if (player.isOp || player.uniqueId.toString() in godSet) {
                val user = app.getUser(player) ?: return@command
                executor(user, args)
                player.sendMessage(Formatting.fine("Успешно!"))
            } else {
                player.sendMessage(Formatting.error("Нет прав."))
            }
        }
    }
}