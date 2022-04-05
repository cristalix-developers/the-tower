package me.reidj.tower.wave

import me.reidj.tower.mod.ModHelper
import me.reidj.tower.pumping.PumpingType
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
            .filter { it.wave != null }
            .forEach {
                if (it.wave!!.isStarting) {
                    if (((System.currentTimeMillis() - it.wave!!.startTime) / 1000 == 40.toLong() || it.wave!!.aliveMobs.isEmpty()))
                        it.wave!!.end()
                    if (time % 20 == 0 && it.health < it.maxHealth) {
                        ModHelper.updateHeartBar(
                            it.maxHealth - maxOf(
                                0.0,
                                it.maxHealth - it.health - it.temporaryPumping[PumpingType.REGEN]!!.getValue()
                            ),
                            it.maxHealth,
                            it
                        )
                    }
                }
            }
    }
}