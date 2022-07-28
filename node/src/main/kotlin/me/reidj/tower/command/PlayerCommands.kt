package me.reidj.tower.command

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.mod.util.command
import me.reidj.tower.HUB
import me.reidj.tower.app
import me.reidj.tower.barrier
import me.reidj.tower.tournament.TournamentManager.isTournamentDay
import me.reidj.tower.util.DialogUtil
import me.reidj.tower.util.GameUtil
import me.reidj.tower.util.GameUtil.QUEUE_SLOTS
import me.reidj.tower.util.GameUtil.error
import me.reidj.tower.util.GameUtil.menu
import me.reidj.tower.util.GameUtil.queue
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService

object PlayerCommands {

    init {
        command("resourcepack") { player, _ -> player.setResourcePack(System.getenv("RESOURCE_PACK"), "9") }

        command("leave") { player, _ -> ITransferService.get().transfer(player.uniqueId, RealmId.of(HUB)) }

        command("normal") { player, _ -> GameUtil.start(player) }

        command("tournament") { player, _ ->
            if (isTournamentDay()) {
                if (queue.size < QUEUE_SLOTS) {
                    Anime.killboardMessage(player, "§aВы добавлены в очередь!")
                    queue.add(player.uniqueId)
                    ModTransfer(queue.size).send("queue:online", player)
                    ModTransfer("tournament.png", QUEUE_SLOTS).send("queue:show", player)
                    if (queue.size == QUEUE_SLOTS) {
                        ModTransfer().send("queue:hide", player)
                        Anime.killboardMessage(player, "Игра начнётся через 2 секунды...")
                        after(2 * 20) {
                            Anime.title(player, "§dНачинаем!")
                            queue.forEach { uuid -> app.getUser(uuid)?.let { GameUtil.ratingGameStart(it) } }
                            queue.clear()
                        }
                    }
                } else {
                    Anime.itemTitle(player, barrier, "Ошибка!", "Игра уже началась", 2.0)
                }
            } else {
                error(player!!, "Турнир ещё не начался!")
            }
        }

        command("play") { player, _ ->
            val user = app.getUser(player) ?: return@command
            if (user.inGame)
                return@command
            menu.open(player)
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