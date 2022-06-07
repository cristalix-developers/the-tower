import banner.Banners
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.entity.EntityLeftClick
import dev.xdark.clientapi.event.render.*
import io.netty.buffer.Unpooled
import mob.MobManager
import player.Statistic
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import tower.BarManager
import tower.Cube
import tower.TowerManager
import java.util.*


lateinit var mod: App
const val NAMESPACE = "cache/animation"

class App : KotlinMod() {

    lateinit var cube: V3
    var inited = false
    var gameActive = false

    override fun onEnable() {
        UIEngine.initialize(this)

        mod = this

        TimeBar
        Statistic

        BarManager
        TowerManager
        Cube

        registerHandler<HealthRender> { isCancelled = true }
        registerHandler<ExpBarRender> { isCancelled = true }
        registerHandler<HungerRender> { isCancelled = true }
        registerHandler<ArmorRender> { isCancelled = true }
        registerHandler<AirBarRender> { isCancelled = true }
        registerHandler<VehicleHealthRender> { isCancelled = true }
        registerHandler<PlayerListRender> { isCancelled = gameActive }

        registerChannel("tower:update-state") {
            gameActive = readBoolean()
            BarManager.healthIndicator!!.enabled = gameActive
            BarManager.protectionIndicator!!.enabled = gameActive
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
                    "",
                    2.0,
                    true
                )
                mod.inited = true

                MobManager

                UIEngine.schedule(1.0) { TowerManager.updateHealth(TowerManager.health, TowerManager.maxHealth) }
            } else {
                Banners.remove(TowerManager.healthBanner!!.uuid)
                inited = false
                UIEngine.overlayContext.removeChild(TimeBar.bar!!)
                MobManager.clear()
            }
        }

        var isArmsLock: Boolean = false

        registerHandler<EntityLeftClick> {
            if (!isArmsLock) {
                isArmsLock = true
                clientApi.clientConnection().sendPayload(
                    "mob:hit",
                    Unpooled.copiedBuffer("${entity.uniqueID}:true", Charsets.UTF_8)
                )
                (entity as EntityLivingBase).updateHealth()
            }
            UIEngine.schedule(3) { isArmsLock = false }
        }
    }
}