package me.reidj.tower.user

import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.laboratory.Research
import me.reidj.tower.laboratory.ResearchType
import me.reidj.tower.tournament.Tournament
import me.reidj.tower.upgrade.SwordType
import me.reidj.tower.upgrade.Upgrade
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.wave.Wave
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.common.SimulatorUser
import ru.kdev.simulatorapi.listener.SessionListener
import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
class User(
    @Transient
    private var id: UUID,
    var maxWavePassed: Int,
    var upgradeTypes: MutableMap<UpgradeType, Upgrade>,
    var researchTypes: MutableMap<ResearchType, Research>,
    var sword: SwordType,
    val tower: Tower,
    var day: Int,
    var dailyClaimTimestamp: Double,
    var lastEnter: Double,
    var tournament: Tournament,
    var isAutoInstallResourcepack: Boolean,
) : SimulatorUser(id), Upgradable {

    @Transient
    var wave: Wave? = null

    @Transient
    var player: Player? = null
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

    fun level() = SessionListener.simulator.run { return@run getLevel() }

    private fun requiredExp() = SessionListener.simulator.run { return@run getNextLevelExp() }

    fun hideFromAll() {
        Bukkit.getOnlinePlayers().filterNotNull().forEach { current ->
            player!!.hidePlayer(app, current)
            current.hidePlayer(app, player)
        }
    }
    fun showToAll() {
        Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }
            .filter { !it.inGame }
            .forEach {
                it.player!!.showPlayer(app, player)
                player!!.showPlayer(app, it.player)
            }
    }

    fun giveTokens(tokens: Int) {
        this.tokens += tokens
        ModTransfer(this.tokens).send("tower:tokens", player)
    }

    fun giveMoney(money: Int) {
        this.money += money
        ModTransfer(this.money).send("tower:money", player)
    }

    fun giveRebirth(rebirth: Int) {
        this.rebirth += rebirth
    }

    fun giveExperience(exp: Int) {
        val prevLevel = level()
        this.exp += exp
        ModTransfer(level(), this.exp, requiredExp()).send("tower:exp", player)
        if (level() > prevLevel) {
            Anime.alert(player!!, "§lПоздравляем!", "Ваш уровень был повышен!\n§7$prevLevel §f ➠ §l${level()}")
            Glow.animate(player!!, .5, GlowColor.BLUE)
        }
    }

    override fun update(user: User, vararg type: me.reidj.tower.user.Upgrade) {
        type.filterIsInstance<UpgradeType>().forEach { ModTransfer(upgradeTypes[it]!!.getValue()).send("user:${it.name.lowercase()}", user.player) }
        type.filterIsInstance<ResearchType>().forEach { ModTransfer(researchTypes[it]!!.getValue()).send("user:${it.name.lowercase()}", user.player) }
    }
}