import dev.xdark.clientapi.event.lifecycle.GameLoop

/**
 * @project tower
 * @author Рейдж
 */
class MobManager {

    private var lastTick = System.currentTimeMillis()
    private val speed = .001

    init {
        mod.registerHandler<GameLoop> {
            if ((System.currentTimeMillis() - wave.time) / 1000 == 40.toLong())
                wave.end()
            val now = System.currentTimeMillis()
            if (now - lastTick > .01 * 1000) {
                lastTick = now
                mod.mobs.forEach { entity ->
                    val dX = mod.cube.x - entity.x
                    val dZ = mod.cube.z - entity.z
                    val rotation =
                        Math.toDegrees(-kotlin.math.atan2(mod.cube.x - entity.x, mod.cube.z - entity.z)).toFloat()

                    entity.rotationYawHead = rotation
                    entity.setYaw(rotation)
                    entity.teleport(entity.x + dX * speed, entity.y, entity.z + dZ * speed)
                }
            }
        }
    }
}