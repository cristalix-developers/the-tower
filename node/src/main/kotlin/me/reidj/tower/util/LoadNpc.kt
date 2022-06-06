package me.reidj.tower.util

import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.protocol.npc.NpcBehaviour
import me.reidj.tower.app
import org.bukkit.event.Listener

object LoadNpc : Listener {

    init {
        Npc.npc {
            behaviour = NpcBehaviour.STARE_AT_PLAYER
            name = "Играть"
            skinUrl = ""
            skinDigest = ""
            location(app.map.getLabel("play"))
            onClick { it.player.performCommand("play") }
        }
        Npc.npc {
            name = "Персонаж"
            behaviour = NpcBehaviour.STARE_AT_PLAYER
            skinUrl = ""
            skinDigest = ""
            location(app.map.getLabel("character"))
            onClick { it.player.performCommand("menu") }
        }
    }
}