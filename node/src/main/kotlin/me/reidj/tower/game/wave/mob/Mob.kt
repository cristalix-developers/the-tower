package me.reidj.tower.game.wave.mob

import me.func.mod.conversation.ModTransfer
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
    var moveSpeed: Float = 0.01f,
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
        ModTransfer(
            uuid.toString(),
            type.typeId.toInt(),
            x,
            y,
            z,
            hp,
            moveSpeed.toDouble()
        ).send("mob:init", player)
    }
}
