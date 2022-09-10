package me.reidj.tower.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import me.func.mod.conversation.ModTransfer
import me.func.mod.dialog.Dialog
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.clientSocket
import me.reidj.tower.game.Game
import me.reidj.tower.protocol.TopPackage
import me.reidj.tower.rank.RankManager
import me.reidj.tower.util.DialogUtil
import me.reidj.tower.util.transfer
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class PlayerCommands {

    private val opened = hashMapOf<UUID, me.func.protocol.dialog.Dialog>()

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
        command("test") { _, args ->
            CoroutineScope(Dispatchers.IO).launch {
                val isSortAscending = args[0].toBoolean()
                val test = clientSocket.writeAndAwaitResponse<TopPackage>(
                    TopPackage(
                        "tournamentMaximumWavePassed",
                        if (isSortAscending) 8 else 4,
                        isSortAscending
                    )
                ).await()
                RankManager.changeRank(isSortAscending, *test.entries.map { it.key.uuid }.toTypedArray())
            }
        }
    }
}