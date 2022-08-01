package me.reidj.tower.command

import me.func.mod.Anime
import me.func.mod.util.command
import me.reidj.tower.HUB
import me.reidj.tower.coroutine
import me.reidj.tower.game.menu
import me.reidj.tower.util.DialogUtil
import me.reidj.tower.withUser
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService

object PlayerCommands {

    init {
        command("resourcepack") { player, _ -> player.setResourcePack(System.getenv("RESOURCE_PACK"), "9") }

        command("leave") { player, _ -> ITransferService.get().transfer(player.uniqueId, RealmId.of(HUB)) }

        command("play") { player, _ ->
            coroutine {
                withUser(player) {
                    if (inGame)
                        return@withUser
                    menu.open(player)
                }
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