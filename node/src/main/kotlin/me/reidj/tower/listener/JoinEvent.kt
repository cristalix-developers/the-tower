package me.reidj.tower.listener

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import me.func.mod.conversation.ModLoader
import me.reidj.tower.app
import me.reidj.tower.user.User
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object JoinEvent : Listener {

    private val startItem = item {
        type = Material.CLAY_BALL
        text("§bИграть")
        nbt("other", "guild_members")
        nbt("click", "play")
    }
    private var backItem = item {
        type = Material.CLAY_BALL
        text("§cВыйти")
        nbt("other", "cancel")
        nbt("click", "leave")
    }
    private var settingsItem: ItemStack = item {
        type = Material.CLAY_BALL
        text("§aПерсонаж")
        nbt("other", "clothes")
        nbt("click", "menu")
    }

    @EventHandler
    fun PlayerJoinEvent.handle() = player.apply {
        teleport(app.spawn)
        SessionListener.simulator.getUser<User>(uniqueId)?.player = player
        inventory.setItem(0, startItem)
        inventory.setItem(4, settingsItem)
        inventory.setItem(8, backItem)

        println(SessionListener.simulator.getUser<User>(uniqueId)!!.money)

        // Отправляем наш мод
        B.postpone(5) { ModLoader.send("tower-mod-bundle.jar", player) }
    }
}