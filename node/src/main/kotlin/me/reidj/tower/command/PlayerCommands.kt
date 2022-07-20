package me.reidj.tower.command

import me.func.mod.Anime
import me.func.mod.selection.choicer
import me.func.mod.util.command
import me.reidj.tower.HUB
import me.reidj.tower.app
import me.reidj.tower.util.DialogUtil
import me.reidj.tower.util.GameUtil
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService

object PlayerCommands {

    init {
        command("resourcepack") { player, _ -> player.setResourcePack(System.getenv("RESOURCE_PACK"), "9") }

        command("leave") { player, _ -> ITransferService.get().transfer(player.uniqueId, RealmId.of(HUB)) }

        command("normal") { player, _ -> GameUtil.start(player) }

        command("tournament") { player, _ -> app.getUser(player)?.let { GameUtil.ratingGameStart(it) } }

        command("play") { player, _ ->
            app.getUser(player)?.let {
                if (it.inGame)
                    return@command
                choicer {
                    title = "Tower Simulator"
                    description = "Выберите под-режим"
                    storage = GameUtil.buttons.toMutableList()
                }.open(player)
            }
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
    }
}