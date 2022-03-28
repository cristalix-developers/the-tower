package me.reidj.tower.mod

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.user.User
import org.bukkit.entity.Player
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object ModHelper {

    fun updateMoney(user: User) = ModTransfer()
        .integer(user.money)
        .send("tower:money", user.player)

    fun updateTokens(user: User, isVisible: Boolean) = ModTransfer()
        .integer(user.tokens)
        .boolean(isVisible)
        .send("tower:tokens", user.player)

    fun updateLevelBar(user: User) {
        println(user.exp)
        ModTransfer()
            .integer(SessionListener.simulator.run { user.getLevel() })
            .integer(user.exp)
            .integer(SessionListener.simulator.run { user.getNextLevelExp() })
            .send("tower:exp", user.player)
    }

    fun updateBarVisible(player: Player) = ModTransfer().send("tower:barvisible", player)
}