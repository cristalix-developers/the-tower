package me.reidj.tower.listener

import me.reidj.tower.app
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class PlayerMoveEvent : Listener {

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (player.location.y <= 80) player.teleport(app.playerDataManager.spawn)
        app.getUser(player)?.let { user -> user.session?.gems?.removeIf { it.pickUp(user, to, 2.2, player.entityId) } }
    }
}