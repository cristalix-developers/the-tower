package mob

import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import mod
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.UIEngine
import util.Vector
import java.util.*
import kotlin.math.pow

/**
 * @project tower
 * @author Рейдж
 */
object MobManager {

    val mobs = mutableMapOf<EntityLivingBase, Mob>()

    private var lastTick = System.currentTimeMillis()
    var moveSpeed = 0.0

    init {
        mod.registerHandler<GameLoop> {
            if (mobs.isEmpty() || !mod.isCubeInitialized())
                return@registerHandler
            val now = System.currentTimeMillis()
            mobs.keys.forEach { mob ->
                if (now - lastTick > mob.aiMoveSpeed * 1000) {
                    lastTick = now
                    mobs.filter { (key, value) -> (mod.cube.x - key.x).pow(2.0) + (mod.cube.z - key.z).pow(2.0) > value.attackRange }
                        .forEach { (key, _) ->
                            val dX = mod.cube.x - key.x
                            val dZ = mod.cube.z - key.z
                            val rotation =
                                Math.toDegrees(-kotlin.math.atan2(mod.cube.x - key.x, mod.cube.z - key.z))
                                    .toFloat()
                            key.rotationYawHead = rotation
                            key.setYaw(rotation)
                            val vector = Vector(dX, 0.0, dZ).normalize().multiply(moveSpeed)
                            key.teleport(key.x + vector.x, key.y, key.z + vector.z)
                        }
                }
            }
        }

        mod.registerChannel("mob:init") {
            val uuid = readId()
            val id = readInt()
            val x = readDouble()
            val y = readDouble()
            val z = readDouble()
            val hp = readDouble()
            val speedAttack = readDouble()
            val moveSpeed = readDouble()
            val attackRange = readDouble()
            val isShooter = readBoolean()
            val mob = Mob(uuid, id, x, y, z, hp, moveSpeed.toFloat(), speedAttack, attackRange, isShooter)
            mobs[mob.create()] = mob
        }

        mod.registerChannel("mob:kill") {
            if (mobs.isEmpty())
                return@registerChannel

            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val text = NetUtil.readUtf8(this)

            val mob = mobs.keys.filter { it.uniqueID == uuid }[0]

            if (text.isNotEmpty()) {
                Banners.create(uuid, mob.x, mob.y + 2, mob.z, text)
                UIEngine.schedule(2) { Banners.remove(uuid) }
            }

            UIEngine.clientApi.minecraft().world.removeEntity(mob)

            mobs.remove(mob)
        }
    }

    fun clear() = mobs.keys.onEach { it.world.removeEntity(it) }.clear()
}