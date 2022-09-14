package me.reidj.tower.listener

import me.func.mod.Banners
import me.func.mod.Banners.location
import me.func.mod.util.after
import me.func.protocol.element.MotionType
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
            item.remove()
            isCancelled = true
            val banner = Banners.new {
                location(item.location.apply { y += 2.0 })
                content = "§d+1 §fСамоцвет"
                opacity = 0.0
            }
            Banners.show(player, banner)
            after(20 * 2) {
                Banners.hide(player, banner)
                Banners.remove(banner.uuid)
            }
        }
    }
}