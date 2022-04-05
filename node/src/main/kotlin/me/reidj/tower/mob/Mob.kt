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
    val x: Double,
    val y: Double,
    val z: Double,
    val damage: Double,
    val type: EntityType
) {

    fun create(player: Player) {
        ModTransfer()
            .string(uuid.toString())
            .integer(type.typeId.toInt())
            .double(x)
            .double(y)
            .double(z)
            .double(hp)
            .send("tower:mobinit", player)
    }
}
