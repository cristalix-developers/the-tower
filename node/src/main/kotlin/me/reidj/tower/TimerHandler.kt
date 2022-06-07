package me.reidj.tower

import me.reidj.tower.ticker.Ticked
import org.bukkit.scheduler.BukkitRunnable

class TimerHandler(private val ticked: List<Ticked>) : BukkitRunnable() {

    var counter = 1

    override fun run() {
        counter++
        ticked.forEach { it.tick(counter) }
    }
}