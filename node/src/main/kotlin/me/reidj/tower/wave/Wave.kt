package me.reidj.tower.wave

import me.func.mod.Anime
import me.reidj.tower.after
import me.reidj.tower.app
import me.reidj.tower.mob.Mob
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
class Wave(
    var isStarting: Boolean,
    var startTime: Long,
    var level: Int,
    val aliveMobs: MutableList<Mob>,
    private val player: Player
) {

    fun start() {
        isStarting = true
        Anime.timer(player, "$level волна", 40)
        repeat(6 + level * 2) {
            Bukkit.getScheduler().runTaskLater(app, {
                SessionListener.simulator.getUser<User>(player.uniqueId)?.apply {
                    if (session == null) {
                        end()
                        return@apply
                    }
                    val v3 = session!!.generators.random()
                    drawMob(v3.x + Math.random() * 4 - 2, v3.y, v3.z + Math.random() * 4 - 2)
                }
            }, minOf(it.toLong() * 12, 400))
        }
    }

    private fun drawMob(x: Double, y: Double, z: Double) {
        aliveMobs.add(Mob {
            hp = 1.0 + level * 0.3
            this.x = x
            this.y = y
            this.z = z
            damage = 0.5 + level * 0.05
        }.create(player))
    }

    fun end() {
        val user = SessionListener.simulator.getUser<User>(player.uniqueId)!!
        val tokens = user.upgradeTypes[UpgradeType.CASH_BONUS_WAVE_PASS]!!.getValue().toInt()
        isStarting = false
        level++
        user.giveTokens(tokens)
        if (level % 10 == 0) {
            Anime.cursorMessage(player, "§e+10 §fмонет")
            user.giveMoney(10)
        }
        startTime = System.currentTimeMillis()
        Anime.counting321(player)
        app.after(3 * 20) { start() }
    }
}