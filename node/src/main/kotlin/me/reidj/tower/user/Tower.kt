package me.reidj.tower.user

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.upgrade.Upgrade
import me.reidj.tower.upgrade.UpgradeType
import org.bukkit.entity.Player

data class Tower(
    var owner: Player? = null,
    var health: Double,
    var maxHealth: Double,
    var upgrades: MutableMap<UpgradeType, Upgrade>
) : Upgradable {

    fun updateHealth() = ModTransfer(health, maxHealth).send("tower:loseheart", owner)

    override fun update(user: User, vararg type: UpgradeType) =
        type.forEach { ModTransfer(upgrades[it]!!.getValue()).send("tower:${it.name.lowercase()}", user.player) }
}
