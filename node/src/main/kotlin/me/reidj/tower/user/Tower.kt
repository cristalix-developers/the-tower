package me.reidj.tower.user

import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.func.protocol.EndStatus
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.data.*
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.upgrade.Upgradable
import me.reidj.tower.util.*
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Tower(
    var owner: Player? = null,
    var health: Double,
    var maxHealth: Double,
    var upgrades: MutableMap<ImprovementType, Improvement>,
    var researches: MutableMap<ResearchType, Research>
) : Upgradable {

    init {
        Anime.createReader("tower:hittower") { player, buffer ->
            val user = app.getUser(player) ?: return@createReader
            val session = user.session ?: return@createReader
            val tower = user.tower ?: return@createReader
            // Если моб есть в списке, то отнимаем хп у башни
            val pair = buffer.toString(Charsets.UTF_8).split(":")
            app.findMob(user, pair[0].encodeToByteArray())?.let { mob ->
                user.run {
                    val damage =
                        mob.damage - session.towerImprovement[ImprovementType.PROTECTION]!!.getValue() - stat.researchType[ResearchType.PROTECTION]!!.getValue()
                    tower.health -= damage
                    Glow.animate(player, .5, GlowColor.RED)
                    Anime.killboardMessage(player, "Вам нанесли §c§l$damage урона")
                    tower.updateHealth()
                    val wave = wave ?: return@createReader
                    val waveLevel = wave.level
                    val reward =
                        (waveLevel * waveLevel - waveLevel) / 4 + stat.researchType[ResearchType.MONEY_BONUS_WAVE_PASS]!!.getValue()

                    // Провожу действия с игроком если он проигрывает
                    if (tower.health <= 0) {
                        if (stat.maximumWavePassed > waveLevel)
                            stat.maximumWavePassed = waveLevel

                        if (isTournament) {
                            TournamentManager.end(this)
                            isTournament = false
                        }

                        player.giveDefaultItems()
                        player.flying(false)
                        showToAll()

                        // Игра закончилась
                        ModTransfer(false).send("tower:update-state", player)

                        Anime.showEnding(player, EndStatus.LOSE, "Волн пройдено:", "$waveLevel")
                        wave.aliveMobs.clear(player)

                        inGame = false

                        giveTokens(-tokens)
                        giveExperience(waveLevel * 3)

                        this.session = null
                        this.wave = null

                        if (reward == 0.0)
                            return@createReader

                        Anime.cursorMessage(
                            player,
                            "§e+${Formatter.toFormat(reward)} §f${reward.plural("монета", "монеты", "монет")}"
                        )

                        giveMoney(reward)
                    }
                }
            }
        }
    }

    fun updateHealth() {
        val upgrade =
            upgrades[ImprovementType.HEALTH]!!.getValue() + researches[ResearchType.HEALTH]!!.getValue()
        if (health == maxHealth)
            health = upgrade
        maxHealth = upgrade
        ModTransfer(health, maxHealth).send("tower:loseheart", owner)
    }

    fun updateBulletDelay() {
        val upgrade =
            upgrades[ImprovementType.BULLET_DELAY]!!.getValue() + researches[ResearchType.BULLET_DELAY]!!.getValue()
        ModTransfer(upgrade).send("tower:bullet_delay", owner)
    }

    fun updateDamage() {
        val upgrade =
            upgrades[ImprovementType.DAMAGE]!!.getValue() + researches[ResearchType.DAMAGE]!!.getValue()
        ModTransfer(upgrade).send("tower:damage", owner)
    }

    fun updateProtection() {
        val upgrade =
            upgrades[ImprovementType.PROTECTION]!!.getValue() + researches[ResearchType.PROTECTION]!!.getValue()
        ModTransfer(upgrade).send("tower:protection", owner)
    }

    override fun update(user: User, vararg pumping: Pumping) {
        pumping.filterIsInstance<ImprovementType>()
            .forEach {
                ModTransfer(upgrades[ImprovementType.valueOf(it.name)]!!.getValue()).send(
                    "tower:${it.name.lowercase()}",
                    user.player
                )
            }
        pumping.filterIsInstance<ResearchType>()
            .forEach {
                ModTransfer(researches[ResearchType.valueOf(it.name)]!!.getValue()).send(
                    "tower:${it.name.lowercase()}",
                    user.player
                )
            }
    }
}
