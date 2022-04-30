package me.reidj.tower.mob

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
data class Mob(
    val uuid: UUID = UUID.randomUUID(),
    var hp: Double = 5.0,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var damage: Double = 1.0,
    var type: EntityType = EntityType.ZOMBIE
) {

    constructor(init: Mob.() -> Unit) : this() {
        this.init()
    }

    fun create(player: Player): Mob {
        ModTransfer()
            .string(uuid.toString())
            .integer(type.typeId.toInt())
            .double(x)
            .double(y)
            .double(z)
            .double(hp)
            .send("mob:init", player)
        return this
    }
}
