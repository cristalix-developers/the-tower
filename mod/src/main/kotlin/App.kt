
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import mob.Mob
import mob.MobManager
import player.BarManager
import player.Statistic
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import java.util.*

lateinit var mod: App
const val NAMESPACE = "tower"
const val FILE_STORE = "http://storage.c7x.ru/reidj/"

class App : KotlinMod() {

    lateinit var cube: V3
    val mobs: MutableList<EntityLivingBase> = mutableListOf()
    var inited = false

    override fun onEnable() {
        mod = this
        UIEngine.initialize(this)

        Cube
        MobManager
        Statistic

        loadTextures(
            load("health_bar.png", "35320C088F83D8890128127"),
            load("energy.png", "35320C088F83D8890128111"),
            load("xp_bar.png", "35320C094F83D8890128111")
        ).thenRun {
            BarManager
        }

        registerHandler<HealthRender> { isCancelled = true }
        registerHandler<ExpBarRender> { isCancelled = true }
        registerHandler<HungerRender> { isCancelled = true }
        registerHandler<ArmorRender> { isCancelled = true }
        registerHandler<AirBarRender> { isCancelled = true }
        registerHandler<VehicleHealthRender> { isCancelled = true }

        mod.registerChannel("tower:init") {
            mod.cube = V3(
                readDouble(),
                readDouble() + 1,
                readDouble()
            )
            mod.inited = true
        }

        registerChannel("tower:mobinit") {
            mobs.add(
                Mob(
                    UUID.fromString(NetUtil.readUtf8(this)),
                    readInt(),
                    readDouble(),
                    readDouble(),
                    readDouble()
                ).create()
            )
        }
    }

    private fun load(path: String, hash: String): RemoteTexture {
        return RemoteTexture(ResourceLocation.of(NAMESPACE, path), hash)
    }
}