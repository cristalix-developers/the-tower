package mob

import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import util.Vector
import java.util.*
import kotlin.math.pow

/**
 * @project tower
 * @author Рейдж
 */
object MobManager {

    val mobs: MutableList<EntityLivingBase> = mutableListOf()

    private var lastTick = System.currentTimeMillis()
    var moveSpeed = 0.0

    init {
        mod.registerHandler<GameLoop> {
            if (mobs.isEmpty())
                return@registerHandler
            val now = System.currentTimeMillis()
            mobs.forEach { mob ->
                if (now - lastTick > mob.aiMoveSpeed * 1000) {
                    lastTick = now
                    mobs.filter { (mod.cube.x - it.x).pow(2.0) + (mod.cube.z - it.z).pow(2.0) > 6.5 }.forEach { entity ->
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

        mod.registerChannel("mob:init") {
            val uuid = UUID.fromString(readUtf8())
            val id = readInt()
            val x = readDouble()
            val y = readDouble()
            val z = readDouble()
            val hp = readDouble()
            val moveSpeed = readDouble()
            mobs.add(Mob(uuid, id, x, y, z, hp, moveSpeed.toFloat()).create())
        }

        mod.registerChannel("mob:kill") {
            if (mobs.isEmpty())
                return@registerChannel

            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val text = NetUtil.readUtf8(this)

            val mob = mobs.filter { it.uniqueID == uuid }[0]

            if (text.isNotEmpty()) {
                Banners.create(uuid, mob.x, mob.y + 2, mob.z, text)
                UIEngine.schedule(2) { Banners.remove(uuid) }
            }

            UIEngine.clientApi.minecraft().world.removeEntity(mob)

            mobs.remove(mob)
        }
    }

    fun clear() = mobs.onEach { it.world.removeEntity(it) }.clear()
}