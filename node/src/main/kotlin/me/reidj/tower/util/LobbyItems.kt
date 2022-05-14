package me.reidj.tower.util

import me.reidj.tower.app
import me.reidj.tower.item
import me.reidj.tower.nbt
import me.reidj.tower.text
import org.bukkit.entity.Player

/**
 * @project tower
 * @author Рейдж
 */
object LobbyItems {

    private val startItem = item {
        text("§bИграть")
        nbt("other", "guild_members")
        nbt("click", "play")
    }
    private var backItem = item {
        text("§cВыйти")
        nbt("other", "cancel")
        nbt("click", "leave")
    }
    private var settingsItem = item {
        text("§aПерсонаж")
        nbt("other", "clothes")
        nbt("click", "menu")
    }

    fun initialActionsWithPlayer(player: Player) = player.apply {
        teleport(app.spawn)
        inventory.clear()
        inventory.setItem(0, startItem)
        inventory.setItem(4, settingsItem)
        inventory.setItem(8, backItem)
    }
}