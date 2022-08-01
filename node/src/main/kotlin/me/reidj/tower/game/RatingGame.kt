package me.reidj.tower.game

import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.mod.util.command
import me.func.protocol.GlowColor
import me.reidj.tower.barrier
import me.reidj.tower.coroutine
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.withUser
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : tower
 * @author : Рейдж
 **/
object RatingGame : Game() {

    private const val QUEUE_SLOTS = 20

    private val queue = mutableListOf<UUID>()

    override fun start(player: Player) {
        if (TournamentManager.isTournamentDay()) {
            coroutine {
                if (TournamentManager.getTournamentPlayers() != 0) {
                    Anime.killboardMessage(player, "Турнирная игра уже началась!")
                    return@coroutine
                }
            }
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
                        queue.forEach { uuid ->
                            super.start(Bukkit.getPlayer(uuid))
                            queue.clear()
                        }
                    }
                } else {
                    error(player, "Турнир ещё не начался!")
                }
            }
        }
    }

    init {
        command("tournament") { player, _ ->
            coroutine {
                withUser(player) {
                    if (tournament.wavePassed.size != 3) {
                        start(player)
                        isTournament = true
                    } else {
                        error(cachedPlayer!!, "У вас закончились попытки!")
                    }
                }
            }
        }

        Anime.createReader("queue:leave") { player, _ -> queueLeave(player) }
    }

    fun queueLeave(player: Player) = queue.removeIf {
        ModTransfer().send("queue:hide", player)
        ModTransfer(queue.size).send("queue:online", player)
        Anime.killboardMessage(player, "§cВы покинули очередь!")
        player.uniqueId in queue
    }

    private fun error(player: Player, subTitle: String) {
        Glow.animate(player, 2.0, GlowColor.RED)
        Anime.itemTitle(player, barrier, "Ошибка", subTitle)
        Anime.close(player)
    }
}