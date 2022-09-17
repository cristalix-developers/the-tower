package tower

import banner.Banner
import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.util.EnumHand
import io.netty.buffer.Unpooled
import mob.MobManager
import mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.sphere
import updateHealth
import util.Formatter
import util.Vector
import kotlin.math.max
import kotlin.math.pow

/**
 * @project tower
 * @author Рейдж
 */
object TowerManager {


    private var lastTickMove = System.currentTimeMillis()
    private var lastTickHit = System.currentTimeMillis()
    private var speedAttack = 0.05 // BULLET_DELAY

    val activeAmmo = mutableListOf<Bullet>()
    var ticksBeforeStrike = 30
    var ticksStrike = 30
    var damage = 0.0
    var health = 5.0
    var maxHealth = 5.0
    var protection = 0.0
    var radius = 0.0

    var healthBanner: Banner? = null

    data class Bullet(
        var x: Double,
        var y: Double,
        var z: Double,
        val target: EntityLivingBase,
        val sphere: Context3D = Context3D(V3(x, y, z)).apply {
            addChild(sphere {
                color = Color(10, 10, 10, 1.0)
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
            if (MobManager.mobs.isEmpty())
                return@registerHandler
            val now = System.currentTimeMillis()
            if (now - lastTickMove > speedAttack * 1000) {
                ticksBeforeStrike--
                lastTickMove = now
                if (ticksBeforeStrike < 0) {
                    ticksBeforeStrike = ticksStrike
                    MobManager.mobs.keys.filter { (it.x - mod.cube.x).pow(2) + (it.z - mod.cube.z).pow(2) <= radius * radius }
                        .filter { entity -> entity.health - activeAmmo.count { it.target == entity } * damage > 0 }
                        .firstOrNull { activeAmmo.add(Bullet(mod.cube.x, mod.cube.y, mod.cube.z, it)) }
                }
                activeAmmo.filter { bullet -> !bullet.target.isEntityAlive }.forEach { it.remove() }
                activeAmmo.filter { (it.x - it.target.x).pow(2.0) + (it.z - it.target.z).pow(2.0) < 1.0 }.forEach {
                    it.target.performHurtAnimation()
                    UIEngine.clientApi.clientConnection().sendPayload(
                        "mob:hit",
                        Unpooled.copiedBuffer("${it.target.uniqueID}:false", Charsets.UTF_8)
                    )
                    activeAmmo.filter { bullet -> !bullet.target.isEntityAlive }.forEach { bullet -> bullet.remove() }
                    it.target.updateHealth()
                    it.remove()
                    activeAmmo.remove(it)
                }
                activeAmmo.forEach {
                    val vector = Vector(it.target.x - it.x, it.target.y + 1.5 - it.y, it.target.z - it.z).normalize()
                        .multiply(0.35)
                    it.sphere.animate(max(speedAttack * .99, 0.001)) {
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
                MobManager.mobs.filter { (key, value) -> (key.x - mod.cube.x).pow(2.0) + (key.z - mod.cube.z).pow(2.0) <= value.attackRange }.keys.forEach {
                    it.swingArm(EnumHand.MAIN_HAND)
                    UIEngine.clientApi.clientConnection()
                        .sendPayload(
                            "tower:hittower",
                            Unpooled.copiedBuffer(it.uniqueID.toString(), Charsets.UTF_8)
                        )
                }
            }
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

        mod.registerChannel("tower:protection") {
            val protect = readDouble()
            if (protect != protection) {
                protection = protect
                (BarManager.protectionBox.children[4] as TextElement).content = "${100 / protection * 100}%"
            }
        }

        mod.registerChannel("tower:radius") {
            radius = readDouble()
        }
    }

    private fun updateHealth(healthUpdate: Double, maxHealthUpdate: Double) {
        if (health == healthUpdate && maxHealth == maxHealthUpdate)
            return

        health = healthUpdate
        maxHealth = maxHealthUpdate

        (BarManager.healthBox.children[3] as TextElement).content =
            "${Formatter.toFormat(health)} из ${Formatter.toFormat(maxHealth)}"

        if (mod.gameActive) {
            Banners.text(
                "§4${Formatter.toFormat(health)} ❤",
                healthBanner!!,
                Banners.banners[healthBanner!!.uuid]!!.second
            )
        }
    }
}
