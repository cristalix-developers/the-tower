package me.reidj.tower.listener

import me.func.mod.Anime
import me.func.mod.Banners
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.after
import me.reidj.tower.app
import me.reidj.tower.user.User
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object ConnectionHandler : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = SessionListener.simulator.getUser<User>(player.uniqueId)!!

        user.player = player
        player.gameMode = GameMode.ADVENTURE
        //LobbyItems.initialActionsWithPlayer(this)

        // Отправляем наш мод
        app.after(1) {
            ModLoader.send("tower-mod-bundle.jar", player)
            ModLoader.send("csc-effect-bundle.jar", player)
            //Anime.loadTexture(player, "https://storage.c7x.dev/func/block.png")
        }

        app.after(20) {
            Banners.banners.values.forEach { Banners.show(player, it) }
        }

        app.after(5) {
            Anime.loadTexture(player, "https://storage.c7x.ru/reidj/tower/2.png")
        }

        app.after(10) {
            ModTransfer("bf30a1df-85de-11e8-a6de-1cb72caa35fd", "2").send("effect:init", player)
        }

        app.after(20) {
            user.giveMoney(-0)
        }
    }
}