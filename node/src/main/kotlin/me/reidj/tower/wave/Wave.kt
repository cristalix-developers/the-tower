package me.reidj.tower.wave

import me.reidj.tower.Mob
import me.reidj.tower.app
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import kotlin.math.roundToInt

/**
 * @project tower
 * @author Рейдж
 */
class Wave(var startTime: Long, var level: Int, val aliveMobs: MutableList<Mob>, val player: Player) {

    private val amount = (level + level / 1.5).roundToInt() // 10 волна - 17 мобов, 40 волна - 67 и тд

    fun start() {
        level++
        player.sendMessage("Началась $level волна")
        app.generators.forEach { drawMob(it.x, it.y, it.z, amount) }
    }

    private fun drawMob(x: Double, y: Double, z: Double, amount: Int) {
        repeat(amount) { aliveMobs.add(Mob(2, x, y, z, 1.0, EntityType.ZOMBIE)) }
    }

    fun end() {
        player.sendMessage("Волна окончена")
        startTime = System.currentTimeMillis()
        start()
    }
}