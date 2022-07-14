package me.reidj.tower.npc

import implario.humanize.Humanize
import me.func.mod.Banners
import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.mod.Npc.skin
import me.func.mod.data.NpcSmart
import me.func.mod.emotion.Emotions
import me.func.mod.util.after
import me.func.protocol.npc.NpcBehaviour
import me.reidj.tower.app
import me.reidj.tower.ticker.Ticked
import me.reidj.tower.tournament.TournamentManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object NpcManager : Ticked {

    private val npcs = mutableMapOf<String, NpcSmart>()

    init {
        NpcType.values().forEach {
            npcs[it.name] = Npc.npc {
                onClick { event -> performCommand(event.player, it.command) }
                location(app.map.getLabel(it.name.lowercase()).clone().add(.5, .0, .5))
                skin(UUID.fromString(it.skin))
                behaviour = NpcBehaviour.STARE_AT_PLAYER
                pitch = it.pitch
                name = it.npcName
            }
        }
    }

    private val guide = npcs[NpcType.GUIDE.name]!!

    fun createNpcWithPlayerSkin(uuid: UUID) = npcs[NpcType.CHARACTER.name]!!.data.skin(uuid)

    private fun performCommand(player: Player, command: String) {
        app.getUser(player)?.run {
            if (isArmLocked)
                return@run
            isArmLocked = true
            player.performCommand(command)
            after(5) { isArmLocked = false }
        }
    }

    override fun tick(vararg args: Int) {
        if (args[0] % 20 != 0) {
            // Обновляю текст на баннерах
            val size = TournamentManager.getOnlinePlayers().size
            val plurals = Humanize.plurals("игрок", "игрока", "игроков", size)
            Bukkit.getOnlinePlayers().forEach { player ->
                Banners.content(
                    player,
                    NpcType.NORMAL.banner!!,
                    "${NpcType.NORMAL.bannerTitle}\n§e${size} $plurals"
                )
                val minutesTotal = TournamentManager.getTimeBefore(TimeUnit.MINUTES)
                val hoursTotal = minutesTotal / 60
                val daysTotal = (hoursTotal / 24) % 24
                val hours = hoursTotal - daysTotal * 24
                val minutes = (hoursTotal - hours) % 60
                Banners.content(
                    player, NpcType.RATING.banner!!, String.format(
                        "%s\n§e%d $plurals\n${
                            if (TournamentManager.isTournamentDay()) "§6До конца %d ${
                                Humanize.plurals(
                                    "час", "часа", "часов",
                                    TournamentManager.getTimeAfter(ChronoUnit.HOURS).toInt()
                                )
                            }" else "§6До начала %d:%d:%d"
                        }",
                        NpcType.RATING.bannerTitle,
                        TournamentManager.getOnlinePlayers().filter { it.isTournament }.size,
                        TournamentManager.getTimeAfter(ChronoUnit.HOURS),
                        daysTotal,
                        hours,
                        minutes
                    )
                )
                val user = app.getUser(player)
                Banners.content(
                    player,
                    NpcType.CHARACTER.banner!!,
                    "${NpcType.CHARACTER.bannerTitle}\n\n§fМонет: §3${user?.money}\n§fВолн пройдено: §3${user?.maxWavePassed}"
                )
            }
        }
        // Анимация нпс
        if (args[0] % 80 == 0) {
            val nearPlayers = Bukkit.getOnlinePlayers()
                .filter { (it.location.x - guide.data.x).pow(2.0) + (it.location.z - guide.data.z).pow(2.0) < 120 }
            Emotions.play(Emotions.WAVE, guide.data.uuid, nearPlayers)
        }
    }
}