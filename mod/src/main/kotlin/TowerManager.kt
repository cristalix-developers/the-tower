import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.JavaMod
import java.util.*
import kotlin.math.pow

/**
 * @project tower
 * @author Рейдж
 */
object TowerManager {

    val activeAmmo = mutableListOf<Bullet>()

    private var lastTickMove = System.currentTimeMillis()
    private var lastTickHit = System.currentTimeMillis()
    private var ticksBeforeStrike = 30
    private var ticksStrike = 30

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
            if (now - lastTickMove > .05 * 1000) {
                ticksBeforeStrike--
                lastTickMove = now
                if (ticksBeforeStrike < 0) {
                    ticksBeforeStrike = ticksStrike
                    mod.mobs.minByOrNull {
                        (it.x - mod.cube.x).pow(2.0) + (it.z - mod.cube.z).pow(2.0)
                    }?.let { activeAmmo.add(Bullet(mod.cube.x, mod.cube.y, mod.cube.z, it)) }
                }
                activeAmmo.removeIf { !it.target.isEntityAlive }
                activeAmmo.filter { (it.x - it.target.x).pow(2.0) + (it.z - it.target.z).pow(2.0) < 1 }.forEach {
                    JavaMod.clientApi.clientConnection().sendPayload(
                        "tower:mobhit",
                        Unpooled.copiedBuffer(it.target.uniqueID.toString(), Charsets.UTF_8)
                    )
                }
                activeAmmo.forEach {
                    val vector = Vector(it.target.x - it.x, it.target.y + 1.5 - it.y, it.target.z - it.z).normalize()
                        .multiply(0.35)
                    it.x += vector.x
                    it.y += vector.y
                    it.z += vector.z
                }
            }
            if (now - lastTickHit > 1 * 1000) {
                lastTickHit = now
                mod.mobs.filter { (it.x - mod.cube.x).pow(2.0) + (it.z - mod.cube.z).pow(2.0) <= 8.0 }.forEach {
                    JavaMod.clientApi.clientConnection()
                        .sendPayload("tower:hittower", Unpooled.copiedBuffer(it.uniqueID.toString(), Charsets.UTF_8))
                }
            }
        }

        mod.registerChannel("tower:mobkill") {
            val uuid = NetUtil.readUtf8(this)
            val mob = mod.mobs.filter { it.uniqueID == UUID.fromString(uuid) }[0]
            mod.mobs.remove(mob)
            JavaMod.clientApi.minecraft().world.removeEntity(mob)
        }

        mod.registerChannel("tower:strike") {
            ticksBeforeStrike = readInt()
            ticksStrike = readInt()
        }
    }
}