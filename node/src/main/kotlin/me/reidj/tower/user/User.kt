package me.reidj.tower.user

import me.reidj.tower.mod.ModHelper
import me.reidj.tower.pumping.Pumping
import me.reidj.tower.pumping.PumpingType
import me.reidj.tower.wave.Wave
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.common.SimulatorUser
import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
class User(val id: UUID, var maxWavePassed: Int, var pumpingTypes: MutableMap<PumpingType, Pumping>) : SimulatorUser() {

    @Transient
    var wave: Wave? = null

    @Transient
    var player: Player? = null

    @Transient
    var inGame: Boolean = false

    @Transient
    lateinit var temporaryPumping: MutableMap<PumpingType, Pumping>

    @Transient
    var health: Double = 0.0

    @Transient
    var maxHealth: Double = 0.0

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
        //val prevLevel = SessionListener.simulator.run { this@User.getLevel() }
        this.exp += exp
        ModHelper.updateLevelBar(this)
        /*if (exp >= prevLevel) {
            Glow.animate(player!!, .5, GlowColor.GREEN)
            Anime.topMessage(
                player!!,
                "§bВаш уровень был повышен!\n§7$prevLevel §f ➠ §l${SessionListener.simulator.run { this@User.getLevel() }}"
            )
        }*/
    }
}