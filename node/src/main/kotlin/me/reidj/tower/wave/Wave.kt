package me.reidj.tower.wave

import me.func.mod.Anime
import me.func.mod.util.after
import me.reidj.tower.app
import me.reidj.tower.mob.Mob
import me.reidj.tower.mob.MobType
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
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
                val session = SessionListener.simulator.getUser<User>(player.uniqueId)?.session ?: return@runTaskLater
                drawMob(session.generators.random().apply {
                    x += Math.random() * 4 - 2
                    z += Math.random() * 4 - 2
                })
            }, minOf(it.toLong() * 12, 400))
        }
    }

    private fun drawMob(location: Location) {
        val type = MobType.values().first { it.wave.contains(level) }
        if (level % 10 == 0) {
            println(1)
            // TODO создание босса
        }
        aliveMobs.add(Mob {
            hp = type.hp + level * 0.3
            damage = type.damage + level * 0.05
            this.type = EntityType.valueOf(type.name)
        }.location(location).create(player))
    }

    fun end() {
        val user = SessionListener.simulator.getUser<User>(player.uniqueId)!!
        isStarting = false
        level++
        user.giveTokens(user.upgradeTypes[UpgradeType.CASH_BONUS_WAVE_PASS]!!.getValue().toInt())
        if (level % 10 == 0) {
            Anime.cursorMessage(player, "§e+10 §fмонет")
            user.giveMoney(10)
        }
        startTime = System.currentTimeMillis()
        Anime.counting321(player)
        after(3 * 20) { start() }
    }
}