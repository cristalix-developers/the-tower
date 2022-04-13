package me.reidj.tower.user

import dev.implario.bukkit.world.Label
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.app
import me.reidj.tower.upgrade.Upgrade
import me.reidj.tower.upgrade.UpgradeType

data class Session(var upgrade: MutableMap<UpgradeType, Upgrade>): Upgradable {
    override fun update(user: User, vararg type: UpgradeType) =
        type.forEach { ModTransfer(upgrade[it]!!.getValue()).send("user:${it.name.lowercase()}", user.player) }

    val arenaSpawn: Label = app.map.getLabel("start").apply { yaw = -90f }
    val cubeLocation = app.map.getLabel("tower").apply {
        x += 0.5
        z += 0.5
    }
    val generators = app.map.getLabels("mob").filter { it.distanceSquared(cubeLocation) < 900 }
}