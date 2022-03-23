package me.reidj.tower.wave

import me.reidj.tower.app
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * @project tower
 * @author Рейдж
 */
class WaveManager : BukkitRunnable() {

    override fun run() {
        Bukkit.getOnlinePlayers().map { app.simulator.getUser<User>(it.uniqueId) }
            .filter { it?.wave != null }
            .filter { (System.currentTimeMillis() - it!!.wave!!.startTime) / 1000 == 40.toLong() }
            .forEach { it?.wave!!.end() }
    }
}