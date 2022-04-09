package me.reidj.tower.user

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.pumping.Upgrade
import me.reidj.tower.pumping.UpgradeType
import org.bukkit.entity.Player

data class Tower(
    var owner: Player? = null,
    var health: Double,
    var maxHealth: Double,
    var upgrades: MutableMap<UpgradeType, Upgrade>
) : Upgradable {
    fun updateHealth() = owner?.apply {
        ModTransfer().double(health).double(maxHealth).send("tower:loseheart", this)
    }

    override fun update(user: User, vararg type: UpgradeType) =
        type.forEach { ModTransfer(upgrades[it]!!.getValue()).send("tower:${it.name.lowercase()}", user.player) }
}
