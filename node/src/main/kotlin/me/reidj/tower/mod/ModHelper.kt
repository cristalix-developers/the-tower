package me.reidj.tower.mod

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player

/**
 * @project tower
 * @author Рейдж
 */
object ModHelper {

    fun sendTokens(tokens: Int, player: Player) = ModTransfer().integer(tokens).send("tower:tokens", player)

    fun sendMoney(money: Int, player: Player) = ModTransfer().integer(money).send("tower:money", player)
}