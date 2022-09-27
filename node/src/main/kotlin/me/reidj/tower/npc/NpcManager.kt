package me.reidj.tower.npc

import implario.humanize.Humanize
import me.func.mod.conversation.data.NpcSmart
import me.func.mod.emotion.Emotions
import me.func.mod.util.after
import me.func.mod.world.Banners
import me.func.mod.world.Npc
import me.func.mod.world.Npc.location
import me.func.mod.world.Npc.onClick
import me.func.protocol.world.npc.NpcBehaviour
import me.reidj.tower.app
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.tournament.TournamentManager.Companion.getTimeAfter
import me.reidj.tower.tournament.TournamentManager.Companion.getTournamentPlayers
import me.reidj.tower.tournament.TournamentManager.Companion.isTournamentDay
import me.reidj.tower.util.Formatter
import org.bukkit.Bukkit
import java.time.temporal.ChronoUnit
import kotlin.math.pow

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class NpcManager : ClockInject {

    companion object {
        val npcs = mutableMapOf<NpcType, NpcSmart>()
    }

    init {
        NpcType.values().forEach {
            npcs[it] = Npc.npc {
                onClick { event ->
                    val player = event.player
                    val user = app.getUser(player) ?: return@onClick
                    if (user.isArmLock)
                        return@onClick
                    user.isArmLock = true
                    it.command(player)
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
            val duration = TournamentManager.getTimeBefore()
            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60
            val plurals = Humanize.plurals("игрок", "игрока", "игроков", size)
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
                    """
                        ${NpcType.CHARACTER.bannerTitle}
                         §fМонеты: §3${Formatter.toFormat(stat.money)}
                         §fСамоцветы: §3${stat.gem}
                         §fОпыт: §3${stat.experience.toInt()}
                         §fВолн пройдено: §3${stat.maximumWavePassed}
                         §fРанг: §3${stat.rank.title}
                    """.trimIndent()
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