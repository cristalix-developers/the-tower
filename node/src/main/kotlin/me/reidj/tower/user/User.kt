package me.reidj.tower.user

import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.coroutine
import me.reidj.tower.getUser
import me.reidj.tower.laboratory.Research
import me.reidj.tower.laboratory.ResearchType
import me.reidj.tower.tournament.RatingType
import me.reidj.tower.tournament.Tournament
import me.reidj.tower.upgrade.SwordType
import me.reidj.tower.upgrade.Upgrade
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.wave.Wave
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.cristalix.simulatorapi.common.SimulatorUser
import ru.cristalix.simulatorapi.listener.SessionListener.simulator
import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
class User(var id: UUID) : SimulatorUser(id), Upgradable {

    var maxWavePassed: Int = 0
    var upgradeTypes = UpgradeType.values().filter { it.isUserUpgrade }.associateWith { Upgrade(it, 1) }.toMutableMap()
    var researchTypes = ResearchType.values().associateWith { Research(it, 1, null) }.toMutableMap()
    var sword: SwordType = SwordType.NONE
    val tower = Tower(
        null,
        5.0,
        5.0,
        UpgradeType.values().filter { !it.isUserUpgrade }.associateWith { Upgrade(it, 1) }.toMutableMap(),
        ResearchType.values().associateWith { Research(it, 1, null) }.toMutableMap(),
    )
    var day = 0
    var dailyClaimTimestamp = 0.0
    var lastEnter = 0.0
    var tournament = Tournament(RatingType.NONE, 0, mutableListOf())
    var isAutoInstallResourcepack = false

    @Transient
    var wave: Wave? = null

    @Transient
    var cachedPlayer: Player? = null
        set(current) {
            tower.owner = current
            field = current
        }

    @Transient
    var inGame = false

    @Transient
    var isTournament = false

    @Transient
    var isArmLocked = false

    @Transient
    var session: Session? = null

    @Transient
    var tokens = 0

    suspend fun level() = simulator.getLevel(id)

    private suspend fun requiredExp() = simulator.getNextLevelExp(id)

    fun hideFromAll() {
        Bukkit.getOnlinePlayers().filterNotNull().forEach { current ->
            cachedPlayer!!.hidePlayer(app, current)
            current.hidePlayer(app, cachedPlayer)
        }
    }

    suspend fun showToAll() {
        Bukkit.getOnlinePlayers().mapNotNull { getUser(it) }
            .filter { !it.inGame }
            .forEach {
                it.cachedPlayer!!.showPlayer(app, cachedPlayer)
                cachedPlayer!!.showPlayer(app, it.cachedPlayer)
            }
    }

    fun giveTokens(tokens: Int) {
        this.tokens += tokens
        ModTransfer(this.tokens).send("tower:tokens", cachedPlayer)
    }

    // TODO Буду ждать когда сделают double
    fun giveMoney(money: Int) {
        this.money += money
        ModTransfer(this.money).send("tower:money", cachedPlayer)
    }

    fun giveRebirth(rebirth: Int) {
        this.rebirth += rebirth
    }

    fun giveExperience(exp: Int) {
        coroutine {
            val prevLevel = level()
            this@User.exp += exp
            ModTransfer(level(), this@User.exp, requiredExp()).send("tower:exp", cachedPlayer)
            if (level() > prevLevel) {
                Anime.alert(
                    cachedPlayer!!,
                    "§lПоздравляем!",
                    "Ваш уровень был повышен!\n§7$prevLevel §f ➠ §l${level()}"
                )
                Glow.animate(cachedPlayer!!, .5, GlowColor.BLUE)
            }
        }
    }

    override fun update(user: User, vararg type: me.reidj.tower.user.Upgrade) {
        type.filterIsInstance<UpgradeType>()
            .forEach { ModTransfer(upgradeTypes[it]!!.getValue()).send("user:${it.name.lowercase()}", user.cachedPlayer) }
        type.filterIsInstance<ResearchType>()
            .forEach { ModTransfer(researchTypes[it]!!.getValue()).send("user:${it.name.lowercase()}", user.cachedPlayer) }
    }
}