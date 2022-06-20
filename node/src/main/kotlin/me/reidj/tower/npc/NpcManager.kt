package me.reidj.tower.npc

import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.mod.util.after
import me.func.protocol.npc.NpcBehaviour
import me.reidj.tower.app
import me.reidj.tower.user.User
import ru.kdev.simulatorapi.listener.SessionListener

const val NAMESPACE = "https://webdata.c7x.dev/textures/skin/"

object NpcManager {

    init {
        app.map.getLabels("game").forEach {
            val data = app.config.getConfigurationSection("npc." + it.tag.split("\\s+")[0])
            Npc.npc {
                onClick { event ->
                    val player = event.player
                    SessionListener.simulator.getUser<User>(player.uniqueId)?.run {
                        if (isArmLocked)
                            return@onClick
                        isArmLocked = true
                        player.performCommand(data.getString("command"))
                        after(5) { isArmLocked = false }
                    }
                }
                location(it.clone().add(.5, 0.0, .5))
                behaviour = NpcBehaviour.STARE_AT_PLAYER
                skinUrl = "$NAMESPACE${data.getString("skin")}"
                skinDigest = data.getString("skin")
                pitch = data.getDouble("pitch").toFloat()
            }
        }
    }
}