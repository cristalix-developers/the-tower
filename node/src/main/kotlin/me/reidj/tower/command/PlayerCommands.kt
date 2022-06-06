package me.reidj.tower.command

import me.func.mod.util.command
import me.reidj.tower.HUB
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService

object PlayerCommands {

    init {
        command("resourcepack") { player, args -> player.setResourcePack(System.getenv("RESOURCE_PACK"), "109") }

        command("leave") { player, _ -> ITransferService.get().transfer(player.uniqueId, RealmId.of(HUB)) }
    }
}