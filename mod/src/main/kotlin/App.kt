
import dev.xdark.clientapi.event.entity.EntityLeftClick
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.*
import io.netty.buffer.Unpooled
import mob.MobManager
import org.lwjgl.input.Keyboard
import player.Statistic
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.debug
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
        mod = this
        UIEngine.initialize(this)


        registerHandler<GameLoop> {
            debug = Keyboard.isKeyDown(Keyboard.KEY_F12)
        }

        Statistic
        TimeBar

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
                MobManager.clear()
            }
        }

        registerHandler<EntityLeftClick> {
            clientApi.clientConnection().sendPayload(
                "mob:hit",
                Unpooled.copiedBuffer("${entity.uniqueID}:true", Charsets.UTF_8)
            )
        }
    }
}