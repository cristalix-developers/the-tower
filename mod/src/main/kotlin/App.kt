
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.render.Tessellator
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import mob.Mob
import mob.MobManager
import org.lwjgl.opengl.GL11
import player.BarManager
import player.Statistic
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

lateinit var mod: App
const val NAMESPACE = "tower"
const val FILE_STORE = "http://storage.c7x.ru/reidj/"

class App : KotlinMod() {

    lateinit var cube: V3
    val mobs: MutableList<EntityLivingBase> = mutableListOf()
    var inited = false
    var gameActive = false

    override fun onEnable() {
        mod = this
        UIEngine.initialize(this)

        Cube
        MobManager
        Statistic
        TowerManager

        val player = clientApi.minecraft().player
        val tessellator: Tessellator = clientApi.tessellator()
        val render = tessellator.bufferBuilder
        val sx = 16.0
        val sy = 16.0
        val sz = 16.0
        val uSize = sx
        val u1 = sx / uSize
        val u2 = u1 + sz / uSize
        val u3 = u2 + sx / uSize
        val u4 = u3 + sz / uSize
        val u5 = u3 + sx / uSize
        val u6 = u4 + sx / uSize

        val v1 = sy / sx
        val v2 = v1 + sz / sx
        val v3 = v2 + sy / sx

        registerHandler<RenderPass> {
            GlStateManager.disableLighting()
            GlStateManager.disableCull()
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GlStateManager.enableTexture2D()
            GlStateManager.disableAlpha()
            clientApi.renderEngine()
                    .bindTexture(ResourceLocation.of("minecraft", "mcpatcher/cit/marioparty/deathcube.png"))
            GL11.glBegin(GL11.GL_POLYGON)

            for (i in 0..360)
                GL11.glVertex3d(sin(Math.toRadians(i.toDouble())), .01, cos(Math.toRadians(i.toDouble())))

            GL11.glEnd()

            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR)
            GlStateManager.shadeModel(GL11.GL_FLAT)
            GlStateManager.enableAlpha()
            GlStateManager.enableCull()
        }

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

        mod.registerChannel("tower:update-state") {
            gameActive = readBoolean()
        }

        registerChannel("tower:mobinit") {
            mobs.add(
                    Mob(
                            UUID.fromString(NetUtil.readUtf8(this)),
                            readInt(),
                            readDouble(),
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