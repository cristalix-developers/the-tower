package me.reidj.tower.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerDropItemEvent

/**
 * @project tower
 * @author Рейдж
 */
object UnusedEvent : Listener {

    @EventHandler
    fun BlockPlaceEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun CraftItemEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun PlayerDropItemEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun FoodLevelChangeEvent.handle() = apply { foodLevel = 20 }

    @EventHandler
    fun EntityDamageEvent.handle() {
        if (entity is Player && (cause == EntityDamageEvent.DamageCause.FALL || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            cancelled = true
    }

    @EventHandler
    fun HangingBreakByEntityEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun BlockPhysicsEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun BlockGrowEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun BlockFadeEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun BlockSpreadEvent.handle() = apply { isCancelled = true }

    @EventHandler
    fun BlockBreakEvent.handle() = apply { isCancelled = true }
}