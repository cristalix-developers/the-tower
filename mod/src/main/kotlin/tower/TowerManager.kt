package tower

import Banners
import Vector
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import mod
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.sphere
import updateNameHealth
import java.util.*
import kotlin.math.pow

/**
 * @project tower
 * @author Рейдж
 */
object TowerManager {

    private val activeAmmo = mutableListOf<Bullet>()

    private var lastTickMove = System.currentTimeMillis()
    private var lastTickHit = System.currentTimeMillis()
    var ticksBeforeStrike = 30
    var ticksStrike = 30
    private var speedAttack = 0.05 // BULLET_DELAY
    private var damage = 0.0
    var health = 5.0
    var maxHealth = 5.0
    var protection = 0.0

    data class Bullet(
        var x: Double,
        var y: Double,
        var z: Double,
        val target: EntityLivingBase,
        val sphere: Context3D = Context3D(V3(x, y, z)).apply {
            addChild(sphere {
                color = Color(255, 255, 255, 1.0)
                size = V3(1.0, 1.0, 1.0)
            })
        }
    ) {

        init {
            UIEngine.worldContexts.add(sphere)
        }

        fun remove() {
            UIEngine.worldContexts.remove(sphere)
            activeAmmo.remove(this)
        }
    }

    init {
        mod.registerHandler<GameLoop> {
            val now = System.currentTimeMillis()
            if (now - lastTickMove > speedAttack * 1000) {
                ticksBeforeStrike--
                lastTickMove = now
                if (ticksBeforeStrike < 0) {
                    ticksBeforeStrike = ticksStrike
                    mod.mobs.filter { (it.x - mod.cube.x).pow(2) + (it.z - mod.cube.z).pow(2) <= 100 }
                        .filter { entity -> entity.health - activeAmmo.count { it.target == entity } * damage > 0 }
                        .firstOrNull { activeAmmo.add(Bullet(mod.cube.x, mod.cube.y, mod.cube.z, it)) }
                }
                activeAmmo.filter { bullet -> !bullet.target.isEntityAlive }.forEach { it.remove() }
                activeAmmo.filter { (it.x - it.target.x).pow(2.0) + (it.z - it.target.z).pow(2.0) < 1 }.forEach {
                    JavaMod.clientApi.clientConnection().sendPayload(
                        "tower:mobhit",
                        Unpooled.copiedBuffer(it.target.uniqueID.toString(), Charsets.UTF_8)
                    )
                    it.target.health -= damage.toFloat()
                    it.target.updateNameHealth()
                    activeAmmo.filter { bullet -> !bullet.target.isEntityAlive }.forEach { bullet -> bullet.remove() }
                }
                activeAmmo.forEach {
                    val vector = Vector(it.target.x - it.x, it.target.y + 1.5 - it.y, it.target.z - it.z).normalize()
                        .multiply(0.35)
                    it.sphere.animate(speedAttack * .99) {
                        offset.x += vector.x
                        offset.y += vector.y
                        offset.z += vector.z
                    }
                    it.x += vector.x
                    it.y += vector.y
                    it.z += vector.z
                }
            }
            if (now - lastTickHit > 1 * 1000) {
                lastTickHit = now
                // TODO тут пиздец надо переехать на сервер
                mod.mobs.filter { (it.x - mod.cube.x).pow(2.0) + (it.z - mod.cube.z).pow(2.0) <= 8.0 }.forEach {
                    JavaMod.clientApi.clientConnection()
                        .sendPayload("tower:hittower", Unpooled.copiedBuffer(it.uniqueID.toString(), Charsets.UTF_8))
                }
            }
        }

        mod.registerChannel("tower:mobkill") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val text = NetUtil.readUtf8(this)
            val mob = mod.mobs.filter { it.uniqueID == uuid }[0]
            Banners.create(uuid, mob.x, mob.y + 2, mob.z, text)
            JavaMod.clientApi.minecraft().world.removeEntity(mob)
            UIEngine.schedule(2) { Banners.remove(uuid) }
            mod.mobs.remove(mob)
        }

        mod.registerChannel("tower:bullet_delay") {
            speedAttack = readDouble()
        }

        mod.registerChannel("tower:damage") {
            damage = readDouble()
        }

        mod.registerChannel("tower:loseheart") {
            updateHealth(readDouble(), readDouble())
        }

        mod.registerChannel("tower:health") {
            updateHealth(readDouble(), readDouble())
        }

        mod.registerChannel("tower:protection") {
            val protect = readDouble()
            if (protect != protection) {
                protection = protect
                BarManager.protectionIndicator?.updatePercentage(protection)
            }
        }
    }

    private fun updateHealth(healthUpdate: Double, maxHealthUpdate: Double) {
        if (healthUpdate != health) {
            health = healthUpdate
            maxHealth = maxHealthUpdate
            BarManager.healthIndicator?.updatePercentage(health, maxHealth)
        }
    }
}