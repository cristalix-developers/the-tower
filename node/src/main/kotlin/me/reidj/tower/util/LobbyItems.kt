package me.reidj.tower.util

import dev.implario.bukkit.item.item
import me.reidj.tower.app
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @project tower
 * @author Рейдж
 */
object LobbyItems {

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

    fun initialActionsWithPlayer(player: Player) = player.apply {
        teleport(app.spawn)
        inventory.setItem(0, startItem)
        inventory.setItem(4, settingsItem)
        inventory.setItem(8, backItem)
    }
}