package me.reidj.tower.wave

import me.reidj.tower.app
import me.reidj.tower.mob.Mob
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToInt

/**
 * @project tower
 * @author Рейдж
 */
class Wave(var startTime: Long, var level: Int, val aliveMobs: MutableList<Mob>, private val player: Player) {

    fun start() {
        level++
        player.sendMessage("Началась $level волна")
        app.generators.forEach { drawMob(it.x, it.y, it.z, (level + level / 1.5).roundToInt()) }
    }

    private fun drawMob(x: Double, y: Double, z: Double, amount: Int) {
        val mob = Mob(UUID.randomUUID(),2, x, y, z, 1.0, EntityType.ZOMBIE)
        aliveMobs.add(mob)
        mob.create(player)
    }

    fun end() {
        player.sendMessage("Волна окончена")
        startTime = System.currentTimeMillis()
        start()
    }
}