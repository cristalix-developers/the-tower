package mob

import dev.xdark.clientapi.entity.EntityLivingBase
import ru.cristalix.uiengine.UIEngine
import updateNameHealth
import java.util.*

class Mob(
    private val uuid: UUID,
    private val id: Int,
    private val x: Double,
    private val y: Double,
    private val z: Double,
    private var hp: Double,
    private val moveSpeed: Float,
    val speedAttack: Double,
    val attackRange: Double,
    val isShooter: Boolean,
) {

    fun create(): EntityLivingBase {
        val mob =
            UIEngine.clientApi.entityProvider().newEntity(id, UIEngine.clientApi.minecraft().world) as EntityLivingBase
        mob.entityId = (Math.random() * Int.MAX_VALUE).toInt()
        mob.setUniqueId(uuid)
        mob.teleport(x, y, z)
        mob.health = hp.toFloat()
        mob.alwaysRenderNameTag = true
        mob.aiMoveSpeed = moveSpeed
        UIEngine.clientApi.minecraft().world.spawnEntity(mob)
        mob.updateNameHealth()
        return mob
    }
}