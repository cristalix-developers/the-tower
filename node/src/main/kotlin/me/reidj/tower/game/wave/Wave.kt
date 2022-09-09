package me.reidj.tower.game.wave

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.tower.app
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.ResearchType
import me.reidj.tower.game.wave.mob.Mob
import me.reidj.tower.game.wave.mob.MobType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Wave(
    var startTime: Long = 0,
    var level: Int,
    val aliveMobs: MutableList<Mob>,
    private val mobs: MutableList<Mob>,
    private val player: Player
) {

    fun start() {
        aliveMobs.clear()
        ModTransfer("$level волна", 40).send("tower:timebar", player)
        repeat(6 + level * 2) {
            Bukkit.getScheduler().runTaskLater(app, {
                val session = (app.getUser(player) ?: return@runTaskLater).session ?: return@runTaskLater
                drawMob(session.generators.random().apply {
                    x += Math.random() * 4 - 2
                    z += Math.random() * 4 - 2
                })
            }, minOf(it.toLong() * 12, 400))
        }
        startTime = System.currentTimeMillis()
    }

    fun end() {
        val user = app.getUser(player) ?: return
        val stat = user.stat
        startTime = 0
        level++
        mobs.clear()
        user.giveTokens(stat.userImprovementType[ImprovementType.CASH_BONUS_WAVE_PASS]!!.getValue() + stat.researchType[ResearchType.CASH_BONUS_WAVE_PASS]!!.getValue())
        if (level % 10 == 0) {
            Anime.cursorMessage(user.player, "§e+10 §fмонет")
            user.giveMoney(10.0)
        }
        Anime.counting321(user.player)
        after(3 * 20) { start() }
    }

    private fun drawMob(location: Location) {
        val hasBoss = level % 10 == 0 && mobs.none { it.isBoss }
        MobType.values()
            .filter { it.wave.any { wave -> level % wave == 0 } }
            .forEach {
                if (hasBoss) {
                    Mob {
                        hp = it.hp + level * 0.3
                        damage = it.damage + level * 0.05
                        type = EntityType.valueOf(it.name)
                        isBoss = true
                    }.location(location).create(player).run {
                        aliveMobs.add(this)
                        mobs.add(this)
                    }
                }
                Mob {
                    hp = it.hp
                    damage = it.damage
                    type = EntityType.valueOf(it.name)
                }.location(location).create(player).run {
                    aliveMobs.add(this)
                    mobs.add(this)
                }
            }
    }
}
