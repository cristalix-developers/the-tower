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
    val uuid: UUID,
    var hp: Double,
    var x: Double,
    var y: Double,
    var z: Double,
    var damage: Double,
    var type: EntityType
) {
    fun create(player: Player) {
        ModTransfer(
            uuid.toString(),
            type.typeId.toInt(),
            x,
            y,
            z,
            hp
        ).send("mob:init", player)
    }
}
