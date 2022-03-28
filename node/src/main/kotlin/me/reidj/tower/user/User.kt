package me.reidj.tower.user

import me.func.mod.conversation.ModTransfer
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

    var wave: Wave? = null
    var player: Player? = null
    var inGame: Boolean = false
    var pumpingTypes: MutableMap<String, PumpingType> =
        PumpingType.values().toSet().associateBy { it.name }.toMutableMap()
    var health: Double = 5.0
    var tokens = 0

    fun giveTokens(tokens: Int, isVisible: Boolean) {
        this.tokens += tokens
        ModTransfer().integer(this.tokens).boolean(isVisible).send("tower:tokens", player)
    }

    fun giveMoney(money: Int) {
        this.money += money
        ModTransfer().integer(this.money).send("tower:money", player)
    }
}