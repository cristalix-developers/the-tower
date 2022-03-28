package me.reidj.tower.user

import me.reidj.tower.mod.ModHelper
import me.reidj.tower.pumping.PumpingType
import me.reidj.tower.wave.Wave
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.common.SimulatorUser

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

    infix fun giveExperience(exp: Int) {
        this.exp += exp
        ModHelper.updateLevelBar(this)
    }
}