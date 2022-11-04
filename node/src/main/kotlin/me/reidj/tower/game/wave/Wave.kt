package me.reidj.tower.game.wave

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.protocol.math.Position
import me.reidj.tower.app
import me.reidj.tower.arena.ArenaManager
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
        startTime = System.currentTimeMillis()
        ModTransfer(40).send("tower:bar", player)
        repeat(1 + level * 2) {
            Bukkit.getScheduler().runTaskLater(app, {
                val session = (app.getUser(player) ?: return@runTaskLater).session ?: return@runTaskLater
                drawMob(session.arena.generators.random().clone().apply {
                    x += Math.random() * 4 - 2
                    z += Math.random() * 4 - 2
                })
            }, minOf(it.toLong() * 75, 400))
        }
    }

    fun end() {
        val user = app.getUser(player) ?: return
        val stat = user.stat
        startTime = 0
        level++
        mobs.clear()
        user.giveTokenWithBooster(
            user.getLevel() * 8 * 0.3 +
                    stat.userImprovementType[ImprovementType.CASH_BONUS_WAVE_PASS]!!.getValue()
                    + stat.researchType[ResearchType.CASH_BONUS_WAVE_PASS]!!.getValue()
        )
        if (level % 10 == 0) {
            Anime.cursorMessage(user.player, "§e+10 §fмонет")
            user.giveMoneyWithBooster(10.0)
        } else if (level == 16) {
            user.session?.let {
                Anime.alert(player, "Поздравляем!", "Вы прошли ${it.arena.arenaNumber} уровень!")
                it.arena = ArenaManager.arenas[ArenaManager.arenas.indexOf(it.arena) + 1]
                val cubeLocation = it.arena.cubeLocation
                ModTransfer(cubeLocation.x, cubeLocation.y, cubeLocation.z).send("tower:map-change", player)
                player.teleport(it.arena.arenaSpawn)
                Anime.overlayText(player, Position.BOTTOM_RIGHT, "Уровень: §3${it.arena.arenaNumber}")
                level = 0
            }
        }
        Anime.counting321(user.player)
        after(3 * 20) { start() }
    }

    private fun drawMob(location: Location) {
        val has = (app.getUser(player) ?: return).session!!.arena.arenaNumber > 1
        val hpFormula = level * if (has) 0.5 else 0.3
        val damageFormula = level * if (has) 0.5 else 0.05
        MobType.values()
            .filter { it.wave.any { wave -> level % wave == 0 } }
            .forEach { mobType ->
                val hasBoss = level % 5 == 0 && mobType.isBoss
                if (hasBoss && mobs.any { it.isBoss })
                    return@forEach
                Mob {
                    hp = mobType.hp + hpFormula
                    damage = mobType.damage + damageFormula
                    type = EntityType.valueOf(mobType.name)
                    speedAttack = mobType.speedAttack
                    moveSpeed = mobType.moveSpeed
                    attackRange = mobType.attackRange
                    isShooter = mobType.isShooter
                    isBoss = hasBoss
                }.location(location).create(player).run {
                    aliveMobs.add(this)
                    mobs.add(this)
                }
            }
    }
}
