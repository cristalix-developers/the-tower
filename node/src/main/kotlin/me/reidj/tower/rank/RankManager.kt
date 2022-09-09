package me.reidj.tower.rank

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.app
import me.reidj.tower.data.RankType
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
object RankManager {

    private val ranks = mutableSetOf<UUID>()

    fun createRank(user: User) {
        val location = user.player.location
        user.stat.run {
            if (rank == RankType.NONE)
                return@run
            ranks.add(uuid)
            ModTransfer(
                uuid.toString(),
                "${rank.name.lowercase()}.png",
                location.x,
                location.y,
                location.z
            ).send("tower:rank", Bukkit.getOnlinePlayers())
        }
    }

    fun showAll(user: User) {
        ranks.mapNotNull { app.getUser(it) }.forEach {
            val location = it.player.location
            it.stat.run {
                ModTransfer(
                    uuid.toString(),
                    "${rank.name.lowercase()}.png",
                    location.x,
                    location.y,
                    location.z
                ).send("tower:rank", user.player)
            }
        }
    }

    fun giveRank(user: User) {
        remove(user.stat.uuid)
        user.stat.rank.increaseRank()
        createRank(user)
    }

    fun remove(uuid: UUID) = ModTransfer(uuid.toString()).send("tower:rank-remove", Bukkit.getOnlinePlayers())
}