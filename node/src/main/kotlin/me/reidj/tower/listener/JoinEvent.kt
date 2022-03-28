package me.reidj.tower.listener

import clepto.bukkit.B
import me.func.mod.conversation.ModLoader
import me.reidj.tower.mod.ModHelper
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
        val user = SessionListener.simulator.getUser<User>(uniqueId)!!

        user.player = this
        LobbyItems.initialActionsWithPlayer(this)

        // Отправляем наш мод
        B.postpone(1) { ModLoader.send("tower-mod-bundle.jar", this) }

        B.postpone(5) {
            ModHelper.updateMoney(user)
            ModHelper.updateLevelBar(user)
        }
    }
}