package me.reidj.tower.listener

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @project tower
 * @author Рейдж
 */
class InteractEvent : Listener {

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (item == null)
            return
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = nmsItem.tag
        if (nmsItem.hasTag() && tag.hasKeyOfType("click", 8))
            player.performCommand(tag.getString("click"))
    }
}