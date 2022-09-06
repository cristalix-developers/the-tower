package me.reidj.tower.game.wave

import me.reidj.tower.app
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.util.clear
import org.bukkit.Bukkit

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class WaveManager : ClockInject {

    override fun run(tick: Int) {
        if (tick % 20 != 0)
            return

        Bukkit.getOnlinePlayers()
            .mapNotNull { app.getUser(it) }
            .filter { it.wave != null && it.session != null && it.tower != null }
            .forEach {
                val wave = it.wave!!
                if (wave.startTime > 0) {
                    if ((System.currentTimeMillis() - wave.startTime) / 1000 == 40.toLong() || wave.aliveMobs.isEmpty()) {
                        wave.aliveMobs.clear(it.player)
                        wave.end()
                    }
                    val session = it.session!!
                    val tower = it.tower!!
                    val value = session.towerImprovement[ImprovementType.REGEN]!!.getValue()
                    if (tower.health < (tower.maxHealth) && value > 0.0) {
                        tower.health = tower.maxHealth - maxOf(0.0, tower.maxHealth - tower.health - value)
                        tower.updateHealth()
                    }
                }
            }
    }
}