package me.reidj.tower.game

import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.choicer
import me.func.mod.util.after
import me.func.protocol.data.status.EndStatus
import me.func.protocol.math.Position
import me.reidj.tower.app
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.ResearchType
import me.reidj.tower.game.wave.Wave
import me.reidj.tower.sword.SwordType
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.user.Session
import me.reidj.tower.user.User
import me.reidj.tower.util.PATH
import me.reidj.tower.util.clear
import me.reidj.tower.util.flying
import me.reidj.tower.util.giveDefaultItems
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
                    onClick { player, _, _ ->
                        if (!(app.getUser(player) ?: return@onClick).armLock()) {
                            player.performCommand("default")
                        }
                    }
                },
                button {
                    title = "Турнир"
                    texture = "${PATH}rating.png"
                    description = "Игроков: §3${TournamentManager.getTournamentPlayers()}"
                    hint("Играть")
                    onClick { player, _, _ ->
                        if (!(app.getUser(player) ?: return@onClick).armLock()) {
                            player.performCommand("tournament")
                        }
                    }
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

            SwordType.valueOf(stat.sword).update(this)

            // Отправляем точки со спавнерами
            session.arena.generators.forEach { label ->
                ModTransfer(label.x, label.y, label.z).send(
                    "mobs:init",
                    player
                )
            }

            Anime.counting321(player)

            inGame = true
            wave = Wave(System.currentTimeMillis(), 1, mutableListOf(), mutableListOf(), player)
            wave!!.start()

            after(3 * 20) {
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

    fun end(user: User) {
        val tower = user.tower ?: return
        val stat = user.stat
        val wave = user.wave ?: return
        val waveLevel = wave.level
        val reward =
            (waveLevel * waveLevel - waveLevel) / 4 + stat.researchType[ResearchType.MONEY_BONUS_WAVE_PASS]!!.getValue()

        if (tower.health <= 0) {
            if (stat.maximumWavePassed > waveLevel) {
                stat.maximumWavePassed = waveLevel
            }

            user.player.giveDefaultItems()
            user.player.flying(false)

            user.showToAll()
            Anime.close(user.player)

            // Игра закончилась
            ModTransfer(false).send("tower:update-state", user.player)

            Anime.showEnding(user.player, EndStatus.LOSE, "Волн пройдено:", "$waveLevel")
            Anime.overlayText(user.player, Position.BOTTOM_RIGHT, "")

            wave.aliveMobs.clear(user.player)
            Gem.bulkRemove(user.connection, user.session!!.gems)

            user.inGame = false

            user.giveToken(-user.tokens)
            user.giveExperienceWithBooster(waveLevel * 3 * 0.3)

            user.session = null
            user.wave = null

            user.giveMoneyWithBooster(reward)
        }
    }
}