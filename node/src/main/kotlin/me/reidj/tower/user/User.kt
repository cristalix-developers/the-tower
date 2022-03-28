package me.reidj.tower.user

import me.func.mod.Anime
import me.func.mod.Glow
import me.func.protocol.GlowColor
import me.reidj.tower.mod.ModHelper
import me.reidj.tower.pumping.PumpingType
import me.reidj.tower.wave.Wave
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.common.SimulatorUser
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
class User(val stat: Stat) : SimulatorUser(stat.id) {

    @Transient
    var wave: Wave? = null

    @Transient
    var player: Player? = null

    @Transient
    var inGame: Boolean = false

    @Transient
    var pumpingTypes: MutableMap<String, PumpingType> =
        PumpingType.values().toSet().associateBy { it.name }.toMutableMap()

    @Transient
    var health: Int = 0

    @Transient
    var maxHealth: Int = 0

    @Transient
    var tokens = 0

    fun giveTokens(tokens: Int, isVisible: Boolean) {
        this.tokens += tokens
        ModHelper.updateTokens(this, isVisible)
    }

    fun giveMoney(money: Int) {
        this.money += money
        ModHelper.updateMoney(this)
    }

    fun giveExperience(exp: Int) {
        val prevLevel = SessionListener.simulator.run { this@User.getLevel() }
        this.exp += exp
        ModHelper.updateLevelBar(this)
        if (exp >= prevLevel) {
            Glow.animate(player!!, .5, GlowColor.GREEN)
            Anime.topMessage(
                player!!,
                "§bВаш уровень был повышен!\n§7$prevLevel §f ➠ §l${SessionListener.simulator.run { this@User.getLevel() }}"
            )
        }
    }
}