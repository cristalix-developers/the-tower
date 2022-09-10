package me.reidj.tower.rank

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.tower.app
import me.reidj.tower.clientSocket
import me.reidj.tower.data.RankType
import me.reidj.tower.protocol.ChangeRankPackage
import me.reidj.tower.protocol.SaveUserPackage
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
            if (user.stat.rank == RankType.NONE)
                return@forEach
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

    fun changeRank(isSortAscending: Boolean, vararg uuids: UUID) = uuids.forEach { uuid ->
        val user = app.getUser(uuid)
        if (user == null) {
            clientSocket.write(ChangeRankPackage(uuid, isSortAscending))
        } else {
            user.stat.run {
                remove(uuid)
                rank = if (isSortAscending) rank.downgradeRank() ?: return@forEach else rank.upgradeRank() ?: return@forEach
                createRank(user)
                after { clientSocket.write(SaveUserPackage(uuid, this)) }
            }
        }
    }

    fun remove(uuid: UUID) = ModTransfer(uuid.toString()).send("tower:rank-remove", Bukkit.getOnlinePlayers())
}