package me.reidj.tower.game

import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.choicer
import me.func.mod.ui.scoreboard.ScoreBoard
import me.func.mod.util.after
import me.reidj.tower.app
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.game.wave.Wave
import me.reidj.tower.sword.SwordType
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.user.Session
import me.reidj.tower.util.PATH
import me.reidj.tower.util.flying
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
interface Game {

    companion object {
        private const val MOVE_SPEED: Double = .01
        private const val CONST_TICKS_BEFORE_STRIKE = 20
        private const val TICKS_BEFORE_STRIKE = 40

        val workshop = item {
            type = Material.CLAY_BALL
            nbt("other", "friend_add")
            text("§bМастерская")
        }

        val menu = choicer {
            title = "Tower Simulator"
            description = "Выберите под-режим"
            buttons(
                button {
                    title = "Обычный"
                    texture = "${PATH}default.png"
                    description = "Игроков: §3${TournamentManager.getOnlinePlayers().size}"
                    hint("Играть")
                    onClick { player, _, _ -> player.performCommand("default") }
                },
                button {
                    title = "Турнир"
                    texture = "${PATH}rating.png"
                    description = "Игроков: §3${TournamentManager.getTournamentPlayers()}"
                    hint("Играть")
                    onClick { player, _, _ -> player.performCommand("tournament") }
                }
            )
        }
    }

    fun start(player: Player) {
        Anime.close(player)
        (app.getUser(player) ?: return).run user@{
            session = Session(tower!!.upgrades)

            val session = session!!

            session.towerImprovement.values.forEach { it.level = 1 }

            hideFromAll()

            player.run {
                inventory.clear()
                inventory.setItem(4, workshop)
                teleport(session.arena.arenaSpawn)
                flying()
            }

            SwordType.valueOf(stat.sword).giveSword(this@user)

            giveTokenWithBooster(80 + getLevel() * 3 * 0.5)

            tower!!.run {
                health = maxHealth
                updateHealth()
                updateBulletDelay()
                updateDamage()
                updateProtection()
                update(
                    this@user,
                    ImprovementType.REGEN,
                    ImprovementType.RADIUS,
                )
            }

            update(this, ImprovementType.SWORD,)

            // Отправляем точки со спавнерами
            session.arena.generators.forEach { label -> ModTransfer(label.x, label.y, label.z).send("mobs:init", player) }

            Anime.counting321(player)

            inGame = true
            after(3 * 20) {
                wave = Wave(System.currentTimeMillis(), 1, mutableListOf(), mutableListOf(), player)
                wave!!.start()

                ScoreBoard.hide(player)
                ScoreBoard.subscribe("game-scoreboard", player)

                // Игра началась
                ModTransfer(
                    true,
                    stat.currentCubeTexture.lowercase(),
                    session.arena.cubeLocation.x,
                    session.arena.cubeLocation.y,
                    session.arena.cubeLocation.z,
                    MOVE_SPEED,
                    TICKS_BEFORE_STRIKE,
                    CONST_TICKS_BEFORE_STRIKE
                ).send("tower:update-state", player)
            }
        }
    }
}