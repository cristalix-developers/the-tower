package me.reidj.tower.util

import me.func.mod.util.nbt
import me.reidj.tower.app
import me.reidj.tower.item
import me.reidj.tower.text
import org.bukkit.entity.Player

/**
 * @project tower
 * @author Рейдж
 */
object LobbyItems {

    private val startItem = item {}.nbt("other", "guild_members").nbt("click", "play").text("§bИграть")
    private var backItem = item {}.nbt("other", "cancel").nbt("click", "leave").text("§cВыйти")
    private var settingsItem = item {}.nbt("other", "clothes").nbt("click", "menu").text("§aПерсонаж")

    fun initialActionsWithPlayer(player: Player) = player.apply {
        teleport(app.spawn)
        inventory.clear()
        inventory.setItem(0, startItem)
        inventory.setItem(4, settingsItem)
        inventory.setItem(8, backItem)
    }
}