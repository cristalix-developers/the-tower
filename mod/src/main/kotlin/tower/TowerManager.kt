package tower

import banner.Banner
import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.util.EnumHand
import hitTower
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

    private var lastTickMoveTowerBullet = System.currentTimeMillis()
    private var lastTickMoveMobBullet = System.currentTimeMillis()
    private var lastTickHit = System.currentTimeMillis()
    private var speedAttack = 0.05 // BULLET_DELAY

    val towerActiveAmmo = mutableListOf<Bullet>()
    val mobActiveAmmo = mutableListOf<Bullet>()

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

        fun move(x: Double, y: Double, z: Double) {
            val vector = Vector(x - this.x, y + 1.5 - this.y, z - this.z).normalize()
                .multiply(0.35)
            sphere.animate(max(speedAttack * .99, 0.001)) {
                offset.x += vector.x
                offset.y += vector.y
                offset.z += vector.z
            }
            this.x += vector.x
            this.y += vector.y
            this.z += vector.z
        }

        fun remove() {
            UIEngine.worldContexts.remove(sphere)
            towerActiveAmmo.remove(this)
        }

        fun removeIf() {
            if (!target.isEntityAlive)
                remove()
        }
    }

    init {
        mod.registerHandler<GameLoop> {
            if (MobManager.mobs.isEmpty())
                return@registerHandler
            val now = System.currentTimeMillis()
            if (now - lastTickMoveTowerBullet > speedAttack * 1000) {
                ticksBeforeStrike--
                lastTickMoveTowerBullet = now
                if (ticksBeforeStrike < 0) {
                    ticksBeforeStrike = ticksStrike
                    // Создаю сферу башни если моб подошёл близко
                    MobManager.mobs.keys.filter { (it.x - mod.cube.x).pow(2) + (it.z - mod.cube.z).pow(2) <= radius * radius }
                        .filter { entity -> entity.health - towerActiveAmmo.count { it.target == entity } * damage > 0 }
                        .firstOrNull { towerActiveAmmo.add(Bullet(mod.cube.x, mod.cube.y, mod.cube.z, it)) }
                }
                towerActiveAmmo.filter { bullet -> !bullet.target.isEntityAlive }.forEach { it.remove() }
                // Если сфера попала в моба
                towerActiveAmmo.filter { (it.x - it.target.x).pow(2.0) + (it.z - it.target.z).pow(2.0) < 1.0 }.forEach {
                    it.target.performHurtAnimation()
                    UIEngine.clientApi.clientConnection().sendPayload(
                        "mob:hit",
                        Unpooled.copiedBuffer("${it.target.uniqueID}:false", Charsets.UTF_8)
                    )
                    it.removeIf()
                    it.target.updateHealth(damage)
                    it.remove()
                    towerActiveAmmo.remove(it)
                }
                // Двигаю сферы башни
                towerActiveAmmo.forEach {
                    val target = it.target
                    it.move(target.x, target.y, target.z)
                }
            } else if (now - lastTickMoveMobBullet > 0.1 * 1000) {
                lastTickMoveMobBullet = now
                // Двигаю сферы мобов
                mobActiveAmmo.onEach {
                    val cube = mod.cube
                    it.move(cube.x, cube.y, cube.z)
                    it.removeIf()
                }
                // Если сфера попала в куб, то наношу урон
                mobActiveAmmo.filter { (it.x - mod.cube.x).pow(2.0) + (it.z - mod.cube.z).pow(2.0) < 1.0 }
                    .forEach {
                        it.target.hitTower()
                        it.remove()
                        mobActiveAmmo.remove(it)
                    }
            }
            MobManager.mobs.values.forEach { mob ->
                if (now - lastTickHit > mob.speedAttack * 1000) {
                    lastTickHit = now
                    // Если моб подошёл достаточно близко к башне, то наношу урон либо спавню сферы
                    MobManager.mobs.filter { (key, value) ->
                        (key.x - mod.cube.x).pow(2.0) + (key.z - mod.cube.z).pow(2.0) <= value.attackRange
                    }.forEach { (key, value) ->
                        if (value.isShooter) {
                            mobActiveAmmo.add(Bullet(key.x, key.y + 2.0, key.z, key))
                        } else {
                            key.run {
                                swingArm(EnumHand.MAIN_HAND)
                                hitTower()
                            }
                        }
                    }
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
                (BarManager.protectionBox.children[4] as TextElement).content = "${Formatter.toFormat(protection)}%"
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
