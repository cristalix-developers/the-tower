package me.reidj.tower.listener

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

/**
 * @project tower
 * @author Рейдж
 */
class UnusedEvent : Listener {

    @EventHandler
    fun PlayerInteractEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockPlaceEvent.handle() { isCancelled = true }

    @EventHandler
    fun CraftItemEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerDropItemEvent.handle() { isCancelled = true }

    @EventHandler
    fun FoodLevelChangeEvent.handle() { foodLevel = 20 }

    @EventHandler
    fun EntityDamageEvent.handle() { isCancelled = true }

    @EventHandler
    fun HangingBreakByEntityEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockPhysicsEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockGrowEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockFadeEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockSpreadEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockBreakEvent.handle() { isCancelled = true }

    @EventHandler
    fun InventoryClickEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerSwapHandItemsEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun InventoryMoveItemEvent.handle() { isCancelled = true }

    @EventHandler
    fun CreatureSpawnEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerAdvancementCriterionGrantEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerArmorStandManipulateEvent.handle() { isCancelled = true }

    @EventHandler
    fun EntityExplodeEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockFromToEvent.handle() { isCancelled = true }

    @EventHandler
    fun InventoryDragEvent.handle() { isCancelled = true }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() { isCancelled = true }
}