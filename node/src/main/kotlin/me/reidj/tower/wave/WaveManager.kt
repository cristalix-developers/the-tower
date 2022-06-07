package me.reidj.tower.wave

import me.reidj.tower.clear
import me.reidj.tower.ticker.Ticked
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object WaveManager : Ticked {

    override fun tick(vararg args: Int) {
        Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId) }
            .filter { it.wave != null && it.session != null }
            .forEach {
                if (it.wave!!.isStarting) {
                    if (((System.currentTimeMillis() - it.wave!!.startTime) / 1000 == 40.toLong() || it.wave!!.aliveMobs.isEmpty())) {
                        it.wave!!.aliveMobs.clear(it.player!!)
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