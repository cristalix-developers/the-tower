package me.reidj.tower.npc

import me.func.mod.Banners
import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.mod.util.after
import me.func.protocol.npc.NpcBehaviour
import me.reidj.tower.app
import me.reidj.tower.ticker.Ticked
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.listener.SessionListener
import java.util.*

private const val WEB_DATA = "https://webdata.c7x.dev/textures/skin/"

object NpcManager : Ticked {

    private val npcStatistic = Npc.npc {
        onClick { event -> performCommand(event.player, "menu") }
        location(app.map.getLabel("character").clone().add(0.5, 0.0, 0.5))
        behaviour = NpcBehaviour.STARE_AT_PLAYER
        pitch = 160f
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
        npcStatistic.data.run {
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
        Bukkit.getOnlinePlayers().forEach { player ->
            NpcType.values().forEach { Banners.content(player, it.banner, it.contentBuilder()) }
        }
    }
}