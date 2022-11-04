package me.reidj.tower.user

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.ui.Glow
import me.func.protocol.data.color.GlowColor
import me.reidj.tower.data.*
import me.reidj.tower.upgrade.Upgradable
import me.reidj.tower.util.toFormat
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Tower(
    var owner: Player? = null,
    var health: Double,
    var maxHealth: Double,
    var upgrades: MutableMap<ImprovementType, Improvement>,
    var researches: MutableMap<ResearchType, Research>
) : Upgradable {

    fun updateHealth() {
        val upgrade =
            upgrades[ImprovementType.HEALTH]!!.getValue() + researches[ResearchType.HEALTH]!!.getValue()
        if (health == maxHealth)
            health = upgrade
        maxHealth = upgrade
        ModTransfer(health, maxHealth).send("tower:loseheart", owner)
    }

    fun updateBulletDelay() {
        val upgrade =
            upgrades[ImprovementType.BULLET_DELAY]!!.getValue() - researches[ResearchType.BULLET_DELAY]!!.getValue()
        ModTransfer(upgrade).send("tower:bullet_delay", owner)
    }

    fun updateDamage() {
        val upgrade =
            upgrades[ImprovementType.DAMAGE]!!.getValue() + researches[ResearchType.DAMAGE]!!.getValue()
        ModTransfer(upgrade).send("tower:damage", owner)
    }

    fun updateProtection() {
        val upgrade =
            upgrades[ImprovementType.PROTECTION]!!.getValue() + researches[ResearchType.PROTECTION]!!.getValue()
        ModTransfer(upgrade).send("tower:protection", owner)
    }

    override fun update(user: User, vararg pumping: Pumping) {
        pumping.filterIsInstance<ImprovementType>()
            .forEach {
                ModTransfer(upgrades[ImprovementType.valueOf(it.name)]!!.getValue()).send(
                    "tower:${it.name.lowercase()}",
                    user.player
                )
            }
        pumping.filterIsInstance<ResearchType>()
            .forEach {
                ModTransfer(researches[ResearchType.valueOf(it.name)]!!.getValue()).send(
                    "tower:${it.name.lowercase()}",
                    user.player
                )
            }
    }

    fun hit(user: User, damage: Double) {
        health -= damage
        Glow.animate(user.player, .5, GlowColor.RED)
        Anime.killboardMessage(user.player, "Вам нанесли §c§l${toFormat(damage)} урона")
        updateHealth()
        user.game.end(user)
    }
}
