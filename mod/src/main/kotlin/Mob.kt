
import dev.xdark.clientapi.entity.EntityLivingBase
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import java.util.*

class Mob(val location: V3, speed: Double) {

    fun create(): EntityLivingBase {
        val mob = UIEngine.clientApi.entityProvider().newEntity(54, UIEngine.clientApi.minecraft().world) as EntityLivingBase
        mob.entityId = (Math.random() * Int.MAX_VALUE).toInt()
        mob.setUniqueId(UUID.randomUUID())
        mob.teleport(location.x, location.y, location.z)
        UIEngine.clientApi.minecraft().world.spawnEntity(mob)
        return mob
    }
}