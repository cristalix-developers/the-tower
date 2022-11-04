package me.reidj.tower.command

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.ui.dialog.Dialog
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.game.Game
import me.reidj.tower.util.DialogUtil
import me.reidj.tower.util.error
import me.reidj.tower.util.transfer

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class PlayerCommands {

    init {
        command("resourcepack") { player, _ -> player.setResourcePack(System.getenv("RESOURCE_PACK"), "000") }
        command("leave") { player, _ -> player.transfer() }
        command("navigator") { player, _ -> ModTransfer().send("func:navigator", player) }
        command("play") { player, _ ->
            val user = app.getUser(player) ?: return@command
            if (user.inGame)
                return@command
            Game.menu.open(player)
        }
        command("tournamentDialog") { player, _ ->
            Dialog.dialog(
                player,
                DialogUtil.tournamentDialog,
                "tournamentPageOne"
            )
        }
        command("tournamentInfo") { player, _ -> Dialog.openDialog(player, "tournamentPageTwo") }
        command("guide") { player, _ ->
            Dialog.dialog(
                player,
                DialogUtil.guideDialog,
                "guidePageOne"
            )
        }
        command("thx") { player, _ ->
            val globalBoosters = app.playerDataManager.globalBoosters
            if (globalBoosters.isEmpty()) {
                player.error("Сейчас нету активных бустеров!")
                return@command
            }
            val user = app.getUser(player) ?: return@command
            globalBoosters.forEach {
                val owner = app.getUser(it.owner) ?: return@command
                val uuids = app.playerDataManager.thanksMap[it.uuid] ?: return@command
                if (uuids.contains(player.uniqueId))
                    return@forEach
                uuids.add(player.uniqueId)
                user.giveGem(2)
                owner.giveGem(3)
            }
            Anime.killboardMessage(
                player,
                "Вы поблагодарили за ${globalBoosters.size} бустер(ов)!"
            )
        }
    }
}