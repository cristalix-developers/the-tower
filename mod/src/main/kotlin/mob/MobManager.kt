package mob

import Vector
import dev.xdark.clientapi.event.lifecycle.GameLoop
import mod
import kotlin.math.pow

/**
 * @project tower
 * @author Рейдж
 */
object MobManager {

    private var lastTick = System.currentTimeMillis()
    var moveSpeed = 0.0

    init {
        mod.registerHandler<GameLoop> {
            val now = System.currentTimeMillis()
            if (now - lastTick > .01 * 1000) {
                lastTick = now
                mod.mobs.filter { (mod.cube.x - it.x).pow(2.0) + (mod.cube.z - it.z).pow(2.0) > 6.5 }.forEach { entity ->
                    val dX = mod.cube.x - entity.x
                    val dZ = mod.cube.z - entity.z
                    val rotation =
                        Math.toDegrees(-kotlin.math.atan2(mod.cube.x - entity.x, mod.cube.z - entity.z)).toFloat()
                    entity.rotationYawHead = rotation
                    entity.setYaw(rotation)
                    val vector = Vector(dX, 0.0, dZ).normalize().multiply(moveSpeed)
                    entity.teleport(entity.x + vector.x, entity.y, entity.z + vector.z)
                }
            }
        }
    }
}