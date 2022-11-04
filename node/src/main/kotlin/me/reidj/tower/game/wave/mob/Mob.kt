package me.reidj.tower.game.wave.mob

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.ResearchType
import me.reidj.tower.user.User
import me.reidj.tower.util.Formatter
import me.reidj.tower.util.plural
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Mob(
    val uuid: UUID = UUID.randomUUID(),
    var hp: Double = 1.0,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var damage: Double = 1.0,
    var speedAttack: Double = 1.0,
    var moveSpeed: Float = 0.01f,
    var attackRange: Double = 8.0,
    var isShooter: Boolean = false,
    var type: EntityType = EntityType.ZOMBIE,
    var isBoss: Boolean = false
) {
    constructor(init: Mob.() -> Unit) : this() {
        this.init()
    }

    private fun location(x: Double, y: Double, z: Double) = apply {
        this.x = x
        this.y = y
        this.z = z
    }

    fun location(location: Location) = location(location.x, location.y, location.z)

    fun create(player: Player) = apply {
        ModTransfer()
            .uuid(uuid)
            .integer(type.typeId.toInt())
            .double(x)
            .double(y)
            .double(z)
            .double(hp)
            .double(speedAttack)
            .double(moveSpeed.toDouble())
            .double(attackRange)
            .boolean(isShooter)
            .send("mob:init", player)
    }

    fun hitMob(user: User, damage: Double, message: String) {
        hp -= damage
        Anime.killboardMessage(user.player, message)
        death(user)
    }

    private fun death(user: User) {
        if (hp <= 0) {
            val token =
                user.stat.userImprovementType[ImprovementType.CASH_BONUS_KILL]!!.getValue() + user.stat.researchType[ResearchType.CASH_BONUS_KILL]!!.getValue()

            user.giveTokenWithBooster(token)

            ModTransfer(
                uuid.toString(), "§b+${Formatter.toFormat(token)} §f${
                    token.plural(
                        "Жетон",
                        "Жетона",
                        "Жетонов"
                    )
                }"
            ).send("mob:kill", user.player)

            user.wave!!.aliveMobs.remove(this)
        }
    }
}
