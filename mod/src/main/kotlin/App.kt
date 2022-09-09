import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.entity.EntityLeftClick
import dev.xdark.clientapi.event.render.PlayerListRender
import io.netty.buffer.Unpooled
import mob.MobManager
import player.Indicator
import player.Statistic
import queue.QueueStatus
import rank.Rank
import ru.cristalix.clientapi.KotlinMod
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

        TimeBar
        Statistic
        Banners
        BarManager
        TowerManager
        Cube
        Indicator
        QueueStatus()
        Rank()

        registerHandler<PlayerListRender> { isCancelled = gameActive }

        registerChannel("tower:update-state") {
            gameActive = readBoolean()
            BarManager.healthIndicator!!.enabled = gameActive
            BarManager.protectionIndicator!!.enabled = gameActive
            Indicator.levelBar.enabled = !gameActive
            Statistic.tokensBox.enabled = gameActive
            if (gameActive) {
                mod.cube = V3(
                    readDouble(),
                    readDouble() + 1,
                    readDouble()
                )
                MobManager.moveSpeed = readDouble()
                TowerManager.ticksBeforeStrike = readInt()
                TowerManager.ticksStrike = readInt()
                TowerManager.healthBanner = Banners.create(
                    UUID.randomUUID(),
                    mod.cube.x,
                    mod.cube.y - 1.25,
                    mod.cube.z,
                    "§4${Formatter.toFormat(TowerManager.health)} ❤",
                    2.0,
                    true
                )
                mod.inited = true

                MobManager
            } else {
                inited = false
                Banners.remove(TowerManager.healthBanner!!.uuid)
                MobManager.clear()
                with(TowerManager.activeAmmo.iterator()) { forEach { _ -> remove() } }
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