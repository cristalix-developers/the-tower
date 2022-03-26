package me.reidj.tower.wave

import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object WaveManager : BukkitRunnable() {

    override fun run() {
        Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId)?.wave }
            .filter { it.isStarting && ((System.currentTimeMillis() - it.startTime) / 1000 == 40.toLong() || it.aliveMobs.isEmpty()) }
            .forEach { it.end() }
    }
}