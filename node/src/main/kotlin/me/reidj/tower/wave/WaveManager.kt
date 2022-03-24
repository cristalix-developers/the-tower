package me.reidj.tower.wave

import me.reidj.tower.app
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * @project tower
 * @author Рейдж
 */
object WaveManager : BukkitRunnable() {

    override fun run() {
        Bukkit.getOnlinePlayers().mapNotNull { app.simulator.getUser<User>(it.uniqueId)?.wave }
            .filter { (System.currentTimeMillis() - it.startTime) / 1000 == 40.toLong() || it.aliveMobs.isEmpty() }
            .forEach { it.end() }
    }
}