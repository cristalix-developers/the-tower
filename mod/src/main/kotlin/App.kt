
import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.entity.EntityLeftClick
import dev.xdark.clientapi.event.render.PlayerListRender
import io.netty.buffer.Unpooled
import mob.MobManager
import player.PlayerHud
import queue.QueueStatus
import rank.Rank
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import tower.BarManager
import tower.Cube
import tower.TowerManager
import util.Formatter
import java.util.*

const val NAMESPACE = "cache/animation"

lateinit var mod: App

class App : KotlinMod() {

    lateinit var cube: V3

    var inited = false
    var gameActive = false

    private var program: Int = 0

    override fun onEnable() {
        UIEngine.initialize(this)

        mod = this

        TimeBar()
        Cube()
        PlayerHud()
        QueueStatus()
        Rank()
        Banners
        BarManager
        TowerManager

        registerHandler<PlayerListRender> { isCancelled = gameActive }

        registerChannel("tower:map-change") {
            Banners.remove(TowerManager.healthBanner!!.uuid)
            cube = V3(readDouble(), readDouble() + 1, readDouble())
            TowerManager.healthBanner = Banners.create(
                UUID.randomUUID(),
                cube.x,
                cube.y - 1.5,
                cube.z,
                "§4${Formatter.toFormat(TowerManager.health)} ❤",
                1.5,
                true
            )
        }

        registerChannel("tower:update-state") {
            gameActive = readBoolean()
            BarManager.healthBox.enabled = gameActive
            BarManager.protectionBox.enabled = gameActive
            BarManager.tokenBox.enabled = gameActive
            if (gameActive) {
                Cube.texture = readUtf8()
                cube = V3(
                    readDouble(),
                    readDouble() + 1,
                    readDouble()
                )
                MobManager.moveSpeed = readDouble()
                TowerManager.ticksBeforeStrike = readInt()
                TowerManager.ticksStrike = readInt()
                TowerManager.healthBanner = Banners.create(
                    UUID.randomUUID(),
                    cube.x,
                    cube.y - 1.25,
                    cube.z,
                    "§4${Formatter.toFormat(TowerManager.health)} ❤",
                    2.0,
                    true
                )
                inited = true

                MobManager
            } else {
                inited = false
                Banners.remove(TowerManager.healthBanner!!.uuid)
                MobManager.clear()
                with(TowerManager.towerActiveAmmo.iterator()) {
                    forEach { ammo ->
                        UIEngine.worldContexts.remove(ammo.sphere)
                        remove()
                    }
                }
            }
        }

        var isArmsLock = false

        registerHandler<EntityLeftClick> {
            if (!isArmsLock) {
                isArmsLock = true
                clientApi.clientConnection().sendPayload(
                    "mob:hit",
                    Unpooled.copiedBuffer("${entity.uniqueID}:true", Charsets.UTF_8)
                )
                if (MobManager.mobs.contains(entity))
                    (entity as EntityLivingBase).updateHealth()
                UIEngine.schedule(3) { isArmsLock = false }
            }
        }
    }
}