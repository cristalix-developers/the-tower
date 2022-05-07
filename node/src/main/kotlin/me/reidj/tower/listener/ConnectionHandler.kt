package me.reidj.tower.listener

import me.func.mod.Anime
import me.func.mod.conversation.ModLoader
import me.reidj.tower.after
import me.reidj.tower.app
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */

private const val NAMESPACE = "http://storage.c7x.ru/reidj/"

object ConnectionHandler : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() = player.apply {
        val user = SessionListener.simulator.getUser<User>(uniqueId)!!

        user.player = this
        gameMode = GameMode.ADVENTURE
        LobbyItems.initialActionsWithPlayer(this)

        // Отправляем наш мод
        app.after(1) {
            ModLoader.send("tower-mod-bundle.jar", this)
        }

        app.after(20) {
            user.giveMoney(-0)
            Anime.loadTextures(this, NAMESPACE + "health_bar.png", NAMESPACE + "energy.png", NAMESPACE + "xp_bar.png")
        }
    }
}