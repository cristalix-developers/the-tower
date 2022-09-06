package me.reidj.tower.user

import clepto.bukkit.world.Label
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.app
import me.reidj.tower.data.Improvement
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.Pumping
import me.reidj.tower.data.ResearchType
import me.reidj.tower.upgrade.Upgradable

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Session(var towerImprovement: MutableMap<ImprovementType, Improvement>) : Upgradable {

    val arenaSpawn: Label = app.worldMeta.getLabel("start").apply { yaw = -90f }
    val cubeLocation = app.worldMeta.getLabel("tower").clone().apply {
        x += 0.5
        z += 0.5
    }
    val generators = app.worldMeta.getLabels("mob").filter { it.distanceSquared(cubeLocation) < 900 }

    fun updateHealth(user: User) {
        val stat = user.stat
        val upgrade =
            towerImprovement[ImprovementType.HEALTH]!!.getValue() + stat.researchType[ResearchType.HEALTH]!!.getValue()
        if (user.health == stat.maxHealth)
            user.health = upgrade
        stat.maxHealth = upgrade
        ModTransfer(user.health, stat.maxHealth).send("tower:loseheart", user.player)
    }

    fun updateBulletDelay(user: User) {
        val upgrade =
            towerImprovement[ImprovementType.BULLET_DELAY]!!.getValue() + user.stat.researchType[ResearchType.BULLET_DELAY]!!.getValue()
        ModTransfer(upgrade).send("tower:bullet_delay", user.player)
    }

    fun updateDamage(user: User) {
        val upgrade =
            towerImprovement[ImprovementType.DAMAGE]!!.getValue() + user.stat.researchType[ResearchType.DAMAGE]!!.getValue()
        ModTransfer(upgrade).send("tower:damage", user.player)
    }

    fun updateProtection(user: User) {
        val upgrade =
            towerImprovement[ImprovementType.PROTECTION]!!.getValue() + user.stat.researchType[ResearchType.PROTECTION]!!.getValue()
        ModTransfer(upgrade).send("tower:protection", user.player)
    }

    override fun update(user: User, vararg pumping: Pumping) {
        pumping.filterIsInstance<ImprovementType>()
            .forEach {
                ModTransfer(towerImprovement[it]!!.getValue()).send(
                    "tower:${it.name.lowercase()}",
                    user.player
                )
            }
    }
}
