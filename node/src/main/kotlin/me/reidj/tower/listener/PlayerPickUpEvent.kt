package me.reidj.tower.listener

import me.func.mod.Banners
import me.func.mod.Banners.location
import me.func.mod.util.after
import me.reidj.tower.app
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPickupItemEvent

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class PlayerPickUpEvent : Listener {

    @EventHandler
    fun PlayerPickupItemEvent.handle() {
        if (item.itemStack.getType() == Material.CLAY_BALL) {
            (app.getUser(player) ?: return).giveGem(1)
            val banner = Banners.new {
                location(item.location)
                content = "§d+1 §fСамоцвет"
            }
            after(20 * 2) { Banners.remove(banner.uuid) }
        }
    }
}