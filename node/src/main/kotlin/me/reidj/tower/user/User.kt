package me.reidj.tower.user

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.app
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

    fun hideFromAll() {
        Bukkit.getOnlinePlayers().filterNotNull().forEach { current ->
            player!!.hidePlayer(app, current)
            current.hidePlayer(app, player)
        }
    }
    fun showToAll() {
        Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId) }
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

    fun giveExperience(exp: Int) {
        //val prevLevel = SessionListener.simulator.run { this@User.getLevel() }
        this.exp += exp
        ModTransfer()
            .integer(SessionListener.simulator.run { getLevel() })
            .integer(exp)
            .integer(SessionListener.simulator.run { getNextLevelExp() })
            .send("tower:exp", player)
        /*if (exp >= prevLevel) {
            Glow.animate(player!!, .5, GlowColor.GREEN)
            Anime.topMessage(
                player!!,
                "§bВаш уровень был повышен!\n§7$prevLevel §f ➠ §l${SessionListener.simulator.run { this@User.getLevel() }}"
            )
        }*/
    }

    override fun update(user: User, vararg type: UpgradeType) =
        type.forEach { ModTransfer(upgradeTypes[it]!!.getValue()).send("user:${it.name.lowercase()}", player) }
}