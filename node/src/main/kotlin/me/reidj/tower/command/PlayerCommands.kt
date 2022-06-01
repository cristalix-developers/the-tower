package me.reidj.tower.command

import me.func.mod.util.command

object PlayerCommands {

    init {
        command("resourcepack") { player, args -> player.setResourcePack(System.getenv("RESOURCE_PACK"), "109") }
    }
}