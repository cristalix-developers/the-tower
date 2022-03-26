package me.reidj.tower.listener

import clepto.bukkit.B
import me.func.mod.conversation.ModLoader
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object JoinEvent : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() = player.apply {
        SessionListener.simulator.getUser<User>(uniqueId)?.player = player
        LobbyItems.initialActionsWithPlayer(player)

        // Отправляем наш мод
        B.postpone(5) { ModLoader.send("tower-mod-bundle.jar", player) }
    }
}