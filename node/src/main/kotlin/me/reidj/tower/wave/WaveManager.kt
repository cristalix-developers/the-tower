package me.reidj.tower.wave

import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object WaveManager : BukkitRunnable() {

    var time: Int = 1

    override fun run() {
        time++
        Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId) }
            .filter { it.wave != null && it.session != null }
            .forEach {
                if (it.wave!!.isStarting) {
                    if (((System.currentTimeMillis() - it.wave!!.startTime) / 1000 == 40.toLong() || it.wave!!.aliveMobs.isEmpty()))
                        it.wave!!.end()
                    if (time % 20 == 0 && it.tower.health < it.tower.maxHealth) {
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