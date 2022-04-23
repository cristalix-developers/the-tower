package me.reidj.tower.listener

import dev.xdark.paper.MaterialRegistry
import me.func.mod.Anime
import me.func.mod.conversation.ModLoader
import me.reidj.tower.after
import me.reidj.tower.app
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
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
            Anime.loadTexture(player, "https://storage.c7x.dev/func/block.png")
        }

        app.after(20) {
            user.giveMoney(-0)
        }

    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        player.location.block.typeId = 400
        println(player.location.block.typeId)
    }
}