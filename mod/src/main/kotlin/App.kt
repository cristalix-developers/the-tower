
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.render.Tessellator
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

        /*registerHandler<RenderPass> {
            GlStateManager.enableTexture2D()
            GlStateManager.enableAlpha()
            clientApi.renderEngine()
                .bindTexture(ResourceLocation.of())
            render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)

            // Front
            render.pos(0.0, sy, 0.0).tex(u2, v3).normal(0f, 0f, -1f).endVertex()
            render.pos(sx, sy, 0.0).tex(u3, v3).normal(0f, 0f, -1f).endVertex()
            render.pos(sx, 0.0, 0.0).tex(u3, v2).normal(0f, 0f, -1f).endVertex()
            render.pos(0.0, 0.0, 0.0).tex(u2, v2).normal(0f, 0f, -1f).endVertex()

            /*render.pos(0.0, sy, 0.0).tex(u2, v3).normal(-1f, 0f, 0f).endVertex()
            render.pos(0.0, 0.0, 0.0).tex(u2, v2).normal(-1f, 0f, 0f).endVertex()
            render.pos(0.0, 0.0, sz).tex(u1, v2).normal(-1f, 0f, 0f).endVertex()
            render.pos(0.0, sy, sz).tex(u1, v3).normal(-1f, 0f, 0f).endVertex()

            render.pos(sx, sy, 0.0).tex(u3, v3).normal(1f, 0f, 0f).endVertex()
            render.pos(sx, sy, sz).tex(u4, v3).normal(1f, 0f, 0f).endVertex()
            render.pos(sx, 0.0, sz).tex(u4, v2).normal(1f, 0f, 0f).endVertex()
            render.pos(sx, 0.0, 0.0).tex(u3, v2).normal(1f, 0f, 0f).endVertex()

            render.pos(0.0, sy, sz).tex(u6, v3).normal(0f, 0f, 1f).endVertex()
            render.pos(0.0, 0.0, sz).tex(u6, v2).normal(0f, 0f, 1f).endVertex()
            render.pos(sx, 0.0, sz).tex(u4, v2).normal(0f, 0f, 1f).endVertex()
            render.pos(sx, sy, sz).tex(u4, v3).normal(0f, 0f, 1f).endVertex()

            render.pos(0.0, 0.0, 0.0).tex(u2, v2).normal(0f, -1f, 0f).endVertex()
            render.pos(sx, 0.0, 0.0).tex(u3, v2).normal(0f, -1f, 0f).endVertex()
            render.pos(sx, 0.0, sz).tex(u3, v1).normal(0f, -1f, 0f).endVertex()
            render.pos(0.0, 0.0, sz).tex(u2, v1).normal(0f, -1f, 0f).endVertex()

            render.pos(0.0, sy, sz).tex(u3, v1).normal(0f, 1f, 0f).endVertex()
            render.pos(sx, sy, sz).tex(u5, v1).normal(0f, 1f, 0f).endVertex()
            render.pos(sx, sy, 0.0).tex(u5, v2).normal(0f, 1f, 0f).endVertex()
            render.pos(0.0, sy, 0.0).tex(u3, v2).normal(0f, 1f, 0f).endVertex()*/

            tessellator.draw()

            GlStateManager.color(1f, 1f, 1f, 1f)
        }*/

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