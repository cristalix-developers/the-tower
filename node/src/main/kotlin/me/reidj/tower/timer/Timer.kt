package me.reidj.tower.timer

import org.bukkit.scheduler.BukkitRunnable

/**
 * @project tower
 * @author Рейдж
 */
class Timer : BukkitRunnable() {

    var time = 0

    override fun run() {
        time++

    }
}