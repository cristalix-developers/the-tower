package me.reidj.tower.wave

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.tower.app
import me.reidj.tower.coroutine
import me.reidj.tower.laboratory.ResearchType
import me.reidj.tower.mob.Mob
import me.reidj.tower.mob.MobType
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.withUser
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

/**
 * @project tower
 * @author Рейдж
 */
class Wave(
    var isStarting: Boolean,
    var startTime: Long,
    var level: Int,
    val aliveMobs: MutableList<Mob>,
    private val mobs: MutableList<Mob>,
    private val player: Player
) {

    fun start() {
        aliveMobs.clear()
        startTime = System.currentTimeMillis()
        isStarting = true
        ModTransfer("$level волна", 40).send("tower:timebar", player)
        repeat(6 + level * 2) {
            Bukkit.getScheduler().runTaskLater(app, {
                coroutine {
                    withUser(player) {
                        val session = session ?: return@withUser
                        drawMob(session.generators.random().apply {
                            x += Math.random() * 4 - 2
                            z += Math.random() * 4 - 2
                        })
                    }
                }
            }, minOf(it.toLong() * 12, 400))
        }
    }

    private val hpStatus = level * 0.3
    private val damageStatus = level * 0.05

    private fun drawMob(location: Location) {
        val has = level % 10 == 0 && mobs.none { it.isBoss }
        (if (has) MobType.values()
            .filter { it.wave.any { wave -> level % wave == 0 && it.isBoss } } else MobType.values()
            .filter { it.wave.any { wave -> level % wave == 0 && !it.isBoss } }).forEach {
            val mob = Mob {
                hp = it.hp + hpStatus
                damage = it.damage + damageStatus
                this.type = EntityType.valueOf(it.name)
                isBoss = has
            }.location(location).create(player)
            aliveMobs.add(mob)
            mobs.add(mob)
        }
    }

    fun end() {
        coroutine {
            withUser(player) {
                isStarting = false
                level++
                mobs.clear()
                giveTokens(
                    upgradeTypes[UpgradeType.CASH_BONUS_WAVE_PASS]!!.getValue()
                        .toInt() + researchTypes[ResearchType.CASH_BONUS_WAVE_PASS]!!.getValue().toInt()
                )
                if (level % 10 == 0) {
                    Anime.cursorMessage(this.cachedPlayer!!, "§e+10 §fмонет")
                    giveMoney(10)
                }
                Anime.counting321(cachedPlayer!!)
                after(3 * 20) { start() }
            }
        }
    }
}