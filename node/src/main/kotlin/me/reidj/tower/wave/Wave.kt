package me.reidj.tower.wave

import clepto.bukkit.B
import me.func.mod.Anime
import me.reidj.tower.app
import me.reidj.tower.mob.Mob
import me.reidj.tower.user.User
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.listener.SessionListener
import java.util.*
import kotlin.math.roundToInt

/**
 * @project tower
 * @author Рейдж
 */
class Wave(var isStarting: Boolean, var startTime: Long, var level: Int, val aliveMobs: MutableList<Mob>, private val player: Player) {

    fun start() {
        isStarting = true
        player.sendTitle("§eПриготовьтесь!", "Началась ${level + 1} волна", 10, 15, 10)
        app.generators.forEach { drawMob(it.x, it.y, it.z, (level + level / 1.5).roundToInt()) }
    }

    private fun drawMob(x: Double, y: Double, z: Double, amount: Int) {
        val mob = Mob(UUID.randomUUID(),1, x, y, z, 1, EntityType.ZOMBIE)
        aliveMobs.add(mob)
        mob.create(player)
    }

    fun end() {
        isStarting = false
        level++
        player.sendTitle("§aПоздравляем!", "Волна завершена", 10, 15, 10)
        if (level % 10 == 0) {
            Anime.cursorMessage(player, "§e+10 §fмонет")
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.giveMoney(10)
        }
        startTime = System.currentTimeMillis()
        B.postpone(3 * 20) { start() }
    }
}