package me.reidj.tower.npc

import implario.humanize.Humanize
import me.func.mod.Banners
import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.mod.data.NpcSmart
import me.func.mod.emotion.Emotions
import me.func.mod.util.after
import me.func.protocol.npc.NpcBehaviour
import me.reidj.tower.app
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.tournament.TournamentManager.getTimeAfter
import me.reidj.tower.tournament.TournamentManager.getTournamentPlayers
import me.reidj.tower.tournament.TournamentManager.isTournamentDay
import me.reidj.tower.util.CategoryMenu
import me.reidj.tower.util.Formatter
import org.bukkit.Bukkit
import java.time.temporal.ChronoUnit
import kotlin.math.pow

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class NpcManager : ClockInject {

    private val npcs = mutableMapOf<NpcType, NpcSmart>()

    init {
        NpcType.values().forEach {
            npcs[it] = Npc.npc {
                onClick { event ->
                    val player = event.player
                    val user = app.getUser(player) ?: return@onClick
                    if (user.isArmLock)
                        return@onClick
                    user.isArmLock = true
                    player.performCommand(it.command)
                    after { user.isArmLock = false }
                }
                location(app.worldMeta.getLabel(it.name.lowercase()).clone().add(.5, .0, .5))
                skinUrl = it.skinUrl
                skinDigest = it.skinDigest
                behaviour = NpcBehaviour.STARE_AT_PLAYER
                pitch = it.pitch
                name = it.npcName
            }
        }
    }

    private val guide = npcs[NpcType.GUIDE]!!

    override fun run(tick: Int) {
        if (tick % 20 == 0) {
            val size = TournamentManager.getOnlinePlayers().size
            val plurals = Humanize.plurals("игрок", "игрока", "игроков", size)
            val duration = TournamentManager.getTimeBefore()
            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60

            val total = days.toString().padStart(2, '0') + "д. " + hours.toString().padStart(2, '0') + "ч. " +
                    minutes.toString().padStart(2, '0') + "м. " + seconds.toString().padStart(2, '0') + "с."

            Bukkit.getOnlinePlayers().forEach { player ->
                Banners.content(player, NpcType.NORMAL.banner!!, "${NpcType.NORMAL.bannerTitle}\n§e${size} $plurals")

                Banners.content(
                    player,
                    NpcType.RATING.banner!!,
                    "${NpcType.RATING.bannerTitle}\n${
                        if (isTournamentDay()) "§e${getTournamentPlayers()} $plurals\n§6До конца ${
                            getTimeAfter(
                                ChronoUnit.HOURS
                            ).toInt()
                        } ч." else "§6До начала\n§6$total"
                    }"
                )
                val stat = (app.getUser(player) ?: return@forEach).stat
                Banners.content(
                    player,
                    NpcType.CHARACTER.banner!!,
                    "${NpcType.CHARACTER.bannerTitle}\n§fМонет: §3${Formatter.toFormat(stat.money)}\n§fВолн пройдено: §3${stat.maximumWavePassed}"
                )
            }
        }
        // Анимация нпс
        if (tick % 80 == 0) {
            val nearPlayers = Bukkit.getOnlinePlayers()
                .filter { (it.location.x - guide.data.x).pow(2.0) + (it.location.z - guide.data.z).pow(2.0) < 120 }
            Emotions.play(Emotions.WAVE.uuid, guide.data.uuid, *nearPlayers.toTypedArray())
        }
    }
}