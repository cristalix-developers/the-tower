package me.reidj.tower.game

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.selection.button
import me.func.mod.selection.choicer
import me.func.mod.util.after
import me.func.mod.util.nbt
import me.reidj.tower.coroutine
import me.reidj.tower.flying
import me.reidj.tower.item
import me.reidj.tower.laboratory.ResearchType
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.Session
import me.reidj.tower.wave.Wave
import me.reidj.tower.withUser
import org.bukkit.entity.Player

/**
 * @project : tower
 * @author : Рейдж
 **/

private const val MOVE_SPEED: Double = .01
private const val CONST_TICKS_BEFORE_STRIKE = 20
private const val TICKS_BEFORE_STRIKE = 40

private val normal = item().nbt("other", "villager")
private val tournament = item().nbt("other", "collection")

val menu = choicer {
    title = "Tower Simulator"
    description = "Выберите под-режим"
    storage = mutableListOf(
        button {
            title = "Обычная"
            item = normal
            onClick { player, _, _ -> player.performCommand("normal") }
        },
        button {
            title = "Турнир"
            item = tournament
            onClick { player, _, _ -> player.performCommand("tournament") }
        }
    )
}

abstract class Game {

    open fun start(player: Player) {
        Anime.close(player)
        coroutine {
            withUser(player) {
                giveTokens(level() * 1000)
                after {
                    hideFromAll()

                    session = Session(tower.upgrades)

                    session?.upgrade?.values?.forEach { upgrade -> upgrade.level = 1 }

                    player.run {
                        inventory.clear()
                        teleport(session?.arenaSpawn)
                        inventory.setItem(4, UpgradeInventory.workshop)
                        flying()
                    }

                    sword.giveSword(this)

                    tower.health = tower.maxHealth
                    tower.updateHealth()
                    tower.update(
                        this,
                        UpgradeType.BULLET_DELAY,
                        ResearchType.BULLET_DELAY,
                        UpgradeType.DAMAGE,
                        UpgradeType.HEALTH,
                        UpgradeType.PROTECTION,
                        UpgradeType.REGEN,
                        UpgradeType.RADIUS
                    )
                    update(
                        this,
                        UpgradeType.CASH_BONUS_KILL,
                        UpgradeType.CASH_BONUS_WAVE_PASS,
                        ResearchType.CASH_BONUS_WAVE_PASS,
                        ResearchType.CASH_BONUS_KILL
                    )

                    // Отправляем точки со спавнерами
                    session?.generators?.forEach { label ->
                        ModTransfer(label.x, label.y, label.z).send(
                            "mobs:init",
                            player
                        )
                    }

                    Anime.counting321(player)

                    // Начинаю волну
                    inGame = true
                }
                after(3 * 20) {
                    val current =
                        Wave(true, System.currentTimeMillis(), 1, mutableListOf(), mutableListOf(), player)
                    wave = current
                    current.start()

                    // Игра началась
                    ModTransfer(
                        true,
                        session!!.cubeLocation.x,
                        session!!.cubeLocation.y,
                        session!!.cubeLocation.z,
                        MOVE_SPEED,
                        TICKS_BEFORE_STRIKE,
                        CONST_TICKS_BEFORE_STRIKE
                    ).send("tower:update-state", player)
                }
            }
        }
    }
}