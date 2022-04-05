package me.reidj.tower.mod

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.pumping.PumpingType
import me.reidj.tower.user.User
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object ModHelper {

    fun updateMoney(user: User) = user.apply {
        ModTransfer()
            .integer(money)
            .send("tower:money", player)
    }

    fun updateTokens(user: User, isVisible: Boolean) = user.apply {
        ModTransfer()
            .integer(tokens)
            .boolean(isVisible)
            .send("tower:tokens", player)
    }

    fun updateLevelBar(user: User) = user.apply {
        ModTransfer()
            .integer(SessionListener.simulator.run { getLevel() })
            .integer(exp)
            .integer(SessionListener.simulator.run { getNextLevelExp() })
            .send("tower:exp", player)
    }

    fun updateBarVisible(player: Player) = ModTransfer().send("tower:barvisible", player)

    fun updateHeartBar(health: Double, maxHealth: Double, user: User) = user.apply {
        this.health = health
        this.maxHealth = maxHealth
        ModTransfer().double(health).double(maxHealth).send("tower:loseheart", player)
    }

    fun updateProtectionBar(user: User) = user.apply {
        ModTransfer().double(pumpingTypes[PumpingType.PROTECTION]!!.getValue()).send("tower:protection", player)
    }

    fun updateAttackSpeed(user: User) = user.apply {
        val defaultAttackSpeed = .05
        val difference = pumpingTypes[PumpingType.ATTACK_SPEED]!!.getValue() - defaultAttackSpeed
        ModTransfer().double(if (difference == 0.0) defaultAttackSpeed else defaultAttackSpeed - difference)
            .send("tower:speedattack", player)
    }

    fun updateDamage(user: User) =
        ModTransfer().double(user.pumpingTypes[PumpingType.DAMAGE]!!.getValue()).send("tower:damgeupdate", user.player)
}