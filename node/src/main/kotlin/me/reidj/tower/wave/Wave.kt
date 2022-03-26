package me.reidj.tower.wave

import clepto.bukkit.B
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
class Wave(var isStarting: Boolean, var startTime: Long, var level: Int, val aliveMobs: MutableList<Mob>, private val player: Player) {

    fun start() {
        level++
        isStarting = true
        player.sendTitle("§eПриготовьтесь!", "Началась $level волна", 10, 15, 10)
        app.generators.forEach { drawMob(it.x, it.y, it.z, (level + level / 1.5).roundToInt()) }
    }

    private fun drawMob(x: Double, y: Double, z: Double, amount: Int) {
        val mob = Mob(UUID.randomUUID(),2, x, y, z, 1.0, EntityType.ZOMBIE)
        aliveMobs.add(mob)
        mob.create(player)
    }

    fun end() {
        isStarting = false
        player.sendTitle("§aПоздравляем!", "Волна завершена", 10, 15, 10)
        startTime = System.currentTimeMillis()
        B.postpone(3 * 20) { start() }
    }
}