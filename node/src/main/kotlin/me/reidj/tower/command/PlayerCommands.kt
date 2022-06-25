package me.reidj.tower.command

import me.func.mod.selection.choicer
import me.func.mod.util.command
import me.reidj.tower.HUB
import me.reidj.tower.game.GameManager
import me.reidj.tower.user.User
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService
import ru.kdev.simulatorapi.listener.SessionListener

object PlayerCommands {

    init {
        command("resourcepack") { player, _ -> player.setResourcePack(System.getenv("RESOURCE_PACK"), "109") }

        command("leave") { player, _ -> ITransferService.get().transfer(player.uniqueId, RealmId.of(HUB)) }

        command("normal") { player, _ -> GameManager.start(player) }

        command("tournament") { player, _ -> GameManager.ratingGameStart(SessionListener.simulator.getUser(player.uniqueId)!!) }

        command("play") { player, _ ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                if (inGame)
                    return@apply
                choicer {
                    title = "Tower Simulator"
                    description = "Выберите под-режим"
                    storage = GameManager.buttons.toMutableList()
                }.open(player)
            }
        }
    }
}