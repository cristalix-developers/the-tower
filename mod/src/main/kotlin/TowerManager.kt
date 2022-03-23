import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.JavaMod
import kotlin.math.pow

/**
 * @project tower
 * @author Рейдж
 */
object TowerManager {

    const val TICKS_BEFORE_STRIKE = 5
    val activeAmmo = mutableListOf<Bullet>()

    private var lastTick = System.currentTimeMillis()
    private val speed = 1
    private var ticksBeforeStrike = 40

    data class Bullet(var x: Double, var y: Double, var z: Double, val target: EntityLivingBase) {

        fun draw() {
            GL11.glLineWidth(20f)
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glVertex3d(0.0 + x, 0.0 + y, 0.0 + z)
            GL11.glVertex3d(0.2 + x, 0.0 + y, 0.0 + z)
            GL11.glVertex3d(0.2 + x, 0.5 + y, 0.5 + z)
            GL11.glEnd()
        }
    }

    init {
        mod.registerHandler<GameLoop> {
            val now = System.currentTimeMillis()
            if (now - lastTick > .05 * 1000) {
                ticksBeforeStrike--
                lastTick = now
                if (ticksBeforeStrike < 0) {
                    ticksBeforeStrike = TICKS_BEFORE_STRIKE
                    mod.mobs.minByOrNull {
                        (it.x - mod.cube.x).pow(2.0) + (it.z - mod.cube.z).pow(2.0)
                    }?.let { activeAmmo.add(Bullet(mod.cube.x, mod.cube.y, mod.cube.z, it)) }
                }
                activeAmmo.removeIf { !it.target.isEntityAlive }
                activeAmmo.filter { (it.x - it.target.x).pow(2.0) + (it.z - it.target.z).pow(2.0) < 1 }.forEach {
                    mod.mobs.remove(it.target)
                    JavaMod.clientApi.minecraft().world.removeEntity(it.target)
                }
                activeAmmo.forEach {
                    it.x += (it.target.x - it.x) * .1
                    it.y += (it.target.y + 1.5 - it.y) * .1
                    it.z += (it.target.z - it.z) * .1
                }
            }
        }
    }
}