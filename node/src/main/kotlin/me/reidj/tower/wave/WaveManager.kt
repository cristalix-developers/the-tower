package me.reidj.tower.wave

import me.reidj.tower.clear
import me.reidj.tower.getUser
import me.reidj.tower.ticker.Ticked
import me.reidj.tower.upgrade.UpgradeType
import org.bukkit.Bukkit

/**
 * @project tower
 * @author Рейдж
 */
object WaveManager : Ticked {

    override suspend fun tick(vararg args: Int) {
        Bukkit.getOnlinePlayers().mapNotNull { getUser(it) }
            .filter { it.wave != null && it.session != null }
            .forEach {
                if (it.wave!!.isStarting) {
                    if (((System.currentTimeMillis() - it.wave!!.startTime) / 1000 == 40.toLong() || it.wave!!.aliveMobs.isEmpty())) {
                        it.wave!!.aliveMobs.clear(it.cachedPlayer!!)
                        it.wave!!.end()
                    }
                    if (args[0] % 20 == 0 && it.tower.health < it.tower.maxHealth && it.session!!.upgrade[UpgradeType.REGEN]!!.getValue() > 0.0) {
                        it.tower.health = it.tower.maxHealth - maxOf(
                            0.0,
                            it.tower.maxHealth - it.tower.health - it.session!!.upgrade[UpgradeType.REGEN]!!.getValue()
                        )
                        it.tower.updateHealth()
                    }
                }
            }
    }
}