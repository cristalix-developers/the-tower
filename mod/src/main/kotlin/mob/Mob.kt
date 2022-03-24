package mob

import dev.xdark.clientapi.entity.EntityLivingBase
import ru.cristalix.uiengine.UIEngine
import java.util.*

class Mob(
    private val uuid: UUID,
    private val id: Int,
    private val x: Double,
    private val y: Double,
    private val z: Double
) {

    fun create(): EntityLivingBase {
        val mob =
            UIEngine.clientApi.entityProvider().newEntity(id, UIEngine.clientApi.minecraft().world) as EntityLivingBase
        mob.entityId = (Math.random() * Int.MAX_VALUE).toInt()
        mob.setUniqueId(uuid)
        mob.teleport(x, y, z)
        UIEngine.clientApi.minecraft().world.spawnEntity(mob)
        return mob
    }
}