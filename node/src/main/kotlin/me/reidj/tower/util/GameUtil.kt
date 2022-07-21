package me.reidj.tower.util

import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.func.mod.selection.button
import me.func.mod.util.after
import me.func.mod.util.nbt
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.barrier
import me.reidj.tower.flying
import me.reidj.tower.item
import me.reidj.tower.laboratory.ResearchType
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.Session
import me.reidj.tower.user.User
import me.reidj.tower.wave.Wave
import org.bukkit.entity.Player

object GameUtil {

    private const val MOVE_SPEED: Double = .01
    private const val CONST_TICKS_BEFORE_STRIKE = 20
    private const val TICKS_BEFORE_STRIKE = 40

    private val normal = item {}.nbt("other", "villager")
    private val tournament = item {}.nbt("other", "collection")

    val buttons = listOf(
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

    fun ratingGameStart(user: User) = user.run {
        if (TournamentManager.isTournamentDay()) {
            if (tournament.wavePassed.size != 3) {
                start(player!!)
                isTournament = true
            } else {
                error(player!!, "У вас закончились попытки!")
            }
        } else {
            error(player!!, "Турнир ещё не начался!")
        }
    }

    fun start(player: Player) {
        val user = app.getUser(player) ?: return

        Anime.close(player)

        user.hideFromAll()

        user.session = Session(user.tower.upgrades)

        user.session?.upgrade?.values?.forEach { upgrade -> upgrade.level = 1 }

        player.run {
            inventory.clear()
            teleport(user.session?.arenaSpawn)
            inventory.setItem(4, UpgradeInventory.workshop)
            flying()
        }

        user.sword.giveSword(user)

        user.tower.health = user.tower.maxHealth
        user.tower.updateHealth()
        user.tower.update(
                user,
                UpgradeType.BULLET_DELAY,
                ResearchType.BULLET_DELAY,
                UpgradeType.DAMAGE,
                UpgradeType.HEALTH,
                UpgradeType.PROTECTION,
                UpgradeType.REGEN,
                UpgradeType.RADIUS
        )
        user.update(
                user,
                UpgradeType.CASH_BONUS_KILL,
                UpgradeType.CASH_BONUS_WAVE_PASS,
                ResearchType.CASH_BONUS_WAVE_PASS,
                ResearchType.CASH_BONUS_KILL
        )

        // Отправляем точки со спавнерами
        user.session?.generators?.forEach { label ->
            ModTransfer(label.x, label.y, label.z).send(
                    "mobs:init",
                    player
            )
        }

        Anime.counting321(player)

        // Начинаю волну
        user.inGame = true
        user.giveTokens(user.level() * 1000)
        after(3 * 20) {
            val current = Wave(true, System.currentTimeMillis(), 1, mutableListOf(), mutableListOf(), player)
            user.wave = current
            current.start()

            // Игра началась
            ModTransfer(
                    true,
                    user.session!!.cubeLocation.x,
                    user.session!!.cubeLocation.y,
                    user.session!!.cubeLocation.z,
                    MOVE_SPEED,
                    TICKS_BEFORE_STRIKE,
                    CONST_TICKS_BEFORE_STRIKE
            ).send("tower:update-state", player)
        }
    }

    private fun error(player: Player, subTitle: String) {
        Glow.animate(player, 2.0, GlowColor.RED)
        Anime.itemTitle(player, barrier, "Ошибка", subTitle)
        Anime.close(player)
    }
}