package me.reidj.tower.rank

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.tower.app
import me.reidj.tower.clientSocket
import me.reidj.tower.data.RankType
import me.reidj.tower.protocol.ChangeRankPackage
import me.reidj.tower.protocol.SaveUserPackage
import me.reidj.tower.protocol.TopPackage
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
object RankManager {

    fun createRank(user: User) {
        user.stat.run {
            if (rank == RankType.NONE) return@run
            ModTransfer(uuid.toString(), "${rank.name.lowercase()}.png").send("tower:rank", Bukkit.getOnlinePlayers())
        }
    }

    fun showAll(user: User) {
        Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }.forEach {
            if (user.stat.rank == RankType.NONE) {
                return@forEach
            }
            val stat = it.stat
            after(5) {
                ModTransfer(stat.uuid.toString(), "${stat.rank.name.lowercase()}.png").send(
                    "tower:rank",
                    user.player
                )
            }
        }
    }

    fun changeRank(vararg topPackage: TopPackage) = topPackage.forEach top@{ pckg ->
        pckg.entries.forEach {
            val uuid = it.key.uuid
            val user = app.getUser(uuid)
            if (user == null) {
                clientSocket.write(ChangeRankPackage(uuid, pckg.isSortAscending))
            } else {
                user.stat.run {
                    rank = if (pckg.isSortAscending) rank.downgradeRank() ?: return@top else rank.upgradeRank()
                        ?: return@top
                    tournamentMaximumWavePassed = 0
                    createRank(user)
                    after { clientSocket.write(SaveUserPackage(uuid, this)) }
                }
            }
        }
    }

    fun remove(uuid: UUID) {
        ModTransfer(uuid.toString()).send("tower:rank-remove", Bukkit.getOnlinePlayers())
    }
}