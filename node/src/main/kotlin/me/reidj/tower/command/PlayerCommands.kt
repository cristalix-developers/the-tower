package me.reidj.tower.command

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.data.RankType
import me.reidj.tower.game.Game
import me.reidj.tower.rank.RankManager
import me.reidj.tower.util.DialogUtil
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
            Anime.dialog(
                player,
                DialogUtil.tournamentDialog,
                "tournamentPageOne"
            )
        }
        command("tournamentInfo") { player, _ -> Anime.openDialog(player, "tournamentPageTwo") }
        command("guide") { player, _ ->
            Anime.dialog(
                player,
                DialogUtil.guideDialog,
                "guidePageOne"
            )
        }
        command("test") { player, args ->
            val user = app.getUser(player) ?: return@command
            user.stat.rank = RankType.valueOf(args[0])
            RankManager.giveRank(user)
        }
    }
}