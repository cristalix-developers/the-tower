
import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.entity.EntityLeftClick
import dev.xdark.clientapi.event.render.PlayerListRender
import io.netty.buffer.Unpooled
import mob.MobManager
import player.PlayerManager
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

    override fun onEnable() {
        UIEngine.initialize(this)

        mod = this

        TimeBar()
        Cube()
        PlayerManager()
        QueueStatus()
        Rank()
        Banners
        BarManager
        TowerManager

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

        registerHandler<PlayerListRender> { isCancelled = gameActive }

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
                TowerManager.run {
                    towerActiveAmmo.removeAll()
                    mobActiveAmmo.removeAll()
                }
            }
        }

        var isArmsLock = false

        registerHandler<EntityLeftClick> {
            if (PlayerManager.swordDamage == 0.0 || clientApi.minecraft().player.heldItemMainhand.isEmpty)
                return@registerHandler
            if (!isArmsLock) {
                isArmsLock = true
                clientApi.clientConnection().sendPayload(
                    "mob:hit",
                    Unpooled.copiedBuffer("${entity.uniqueID}:true", Charsets.UTF_8)
                )
                if (entity in MobManager.mobs) {
                    (entity as EntityLivingBase).updateHealth(PlayerManager.swordDamage)
                }
                UIEngine.schedule(3) { isArmsLock = false }
            }
        }
    }
}