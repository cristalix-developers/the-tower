package me.reidj.tower.game

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import me.func.mod.util.after
import me.func.mod.world.Banners
import me.reidj.tower.app
import me.reidj.tower.user.User
import net.minecraft.server.v1_12_R1.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import kotlin.math.abs

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class Gem(location: Location) {

    private val gem = CraftItemStack.asNMSCopy(item {
        type = Material.CLAY_BALL
        nbt("tower", "gem")
    })

    private var entityItem: EntityItem

    init {
        entityItem = EntityItem(app.getNMSWorld(), location.x, location.y, location.z, gem)
    }

    companion object {
        fun bulkRemove(connection: PlayerConnection, gems: Set<Gem>) {
            val idsToRemove = IntArray(gems.size)
            with(gems.iterator()) {
                for (i in idsToRemove.indices) {
                    idsToRemove[i] = this.next().entityItem.getId()
                }
            }
            connection.sendPacket(PacketPlayOutEntityDestroy(idsToRemove))
        }
    }

    private fun remove(connection: PlayerConnection) =
        connection.sendPacket(PacketPlayOutEntityDestroy(intArrayOf(entityItem.getId())))

    fun create(connection: PlayerConnection) {
        connection.sendPacket(PacketPlayOutSpawnEntity(entityItem, 2))
        connection.sendPacket(PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.dataWatcher, false))
    }

    fun pickUp(user: User, location: Location, radius: Double, collectorId: Int): Boolean {
        if (abs(location.getX() - entityItem.x) > radius
            || abs(location.getY() - entityItem.y) > radius
            || abs(location.getZ() - entityItem.z) > radius
        ) return false

        user.connection.sendPacket(PacketPlayOutCollect(entityItem.getId(), collectorId, 1))
        user.giveGemWithBooster(1)

        val banner = Banners.new {
            x = entityItem.x
            y = entityItem.y
            z = entityItem.z
            content = "§d+1 §fСамоцвет"
            opacity = 0.0
            watchingOnPlayer = true
        }
        Banners.show(user.player, banner)

        after(20 * 2) {
            Banners.hide(user.player, banner)
            Banners.remove(banner.uuid)
        }

        B.postpone(30) { remove(user.connection) }
        return true
    }
}