package me.reidj.tower.npc

import me.func.mod.Banners
import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.mod.util.after
import me.func.protocol.npc.NpcBehaviour
import me.reidj.tower.app
import me.reidj.tower.ticker.Ticked
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.listener.SessionListener
import java.util.*
import java.util.concurrent.TimeUnit

private const val WEB_DATA = "https://webdata.c7x.dev/textures/skin/"

object NpcManager : Ticked {

    private val character = Npc.npc {
        onClick { event -> performCommand(event.player, "menu") }
        location(app.map.getLabel("character").clone().add(0.5, 0.0, 0.5))
        behaviour = NpcBehaviour.STARE_AT_PLAYER
        pitch = 160f
        name = "§eНАЖМИТЕ ДЛЯ ПРОСМОТРА"
    }

    init {
        NpcType.values().forEach {
            Npc.npc {
                onClick { event -> performCommand(event.player, it.command) }
                location(it.location.clone().add(.5, .0, .5))
                behaviour = NpcBehaviour.STARE_AT_PLAYER
                skinUrl = "$WEB_DATA${it.skin}"
                skinDigest = it.skin
                pitch = it.pitch
            }
        }
    }

    fun createNpcWithPlayerSkin(uuid: UUID) {
        character.data.run {
            skinUrl = "$WEB_DATA${uuid}"
            skinDigest = uuid.toString()
        }
    }

    private fun performCommand(player: Player, command: String) {
        SessionListener.simulator.getUser<User>(player.uniqueId)?.run {
            if (isArmLocked)
                return@run
            isArmLocked = true
            player.performCommand(command)
            after(5) { isArmLocked = false }
        }
    }

    override fun tick(vararg args: Int) {
        if (args[0] % 20 != 0)
            return
        println("days ${TournamentManager.getTimeAfter(TimeUnit.DAYS)}")
        println("hours ${TournamentManager.getTimeAfter(TimeUnit.HOURS)}")
        println("seconds ${TournamentManager.getTimeAfter(TimeUnit.SECONDS)}")
        Bukkit.getOnlinePlayers().forEach { player ->
            Banners.content(
                player,
                NpcType.NORMAL.banner,
                "${NpcType.NORMAL.title}\n§e${TournamentManager.getOnlinePlayers().size} игроков"
            )
            Banners.content(
                player, NpcType.RATING.banner, String.format(
                    "%s\n${if (TournamentManager.isTournamentDay()) "§e%d игроков\n§6До конца %d:%d:%d" else "§6До начала %d:%d:%d"}",
                    NpcType.RATING.title,
                    TournamentManager.getOnlinePlayers().filter { it.isTournament }.size,
                    TournamentManager.getTimeAfter(TimeUnit.DAYS),
                    TournamentManager.getTimeAfter(TimeUnit.HOURS),
                    TournamentManager.getTimeAfter(TimeUnit.SECONDS),
                    TournamentManager.getTimeBefore(TimeUnit.DAYS),
                    TournamentManager.getTimeBefore(TimeUnit.HOURS),
                    TournamentManager.getTimeBefore(TimeUnit.SECONDS),
                )
            )
            val user = SessionListener.simulator.getUser<User>(player.uniqueId)
            Banners.content(
                player,
                NpcType.CHARACTER.banner,
                "${NpcType.CHARACTER.title}\n\n§fМонет: §3${user?.money}\n§fВолн пройдено: §3${user?.maxWavePassed}"
            )
        }
    }
}