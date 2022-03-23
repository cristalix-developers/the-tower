
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_POLYGON
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import kotlin.math.cos
import kotlin.math.sin

lateinit var mod: App
lateinit var wave: Wave

class App : KotlinMod() {

    lateinit var cube: V3
    var coords: ArrayList<V3> = arrayListOf()
    val mobs: MutableList<EntityLivingBase> = mutableListOf()
    var inited = false

    override fun onEnable() {
        mod = this
        UIEngine.initialize(this)

        registerChannel("mobs:init") {
            coords.add(V3(readDouble(), readDouble(), readDouble()))
        }

        // Начинать 1 волну через 2 секунды после входа игрока
        UIEngine.schedule(2) {
            //wave = Wave(System.currentTimeMillis(), 0, coords, cube)
            //wave.start()
            MobManager()
        }

        registerChannel("tower:init") {
            cube = V3(
                readDouble(),
                readDouble() + 1,
                readDouble()
            )
            inited = true
        }

        var angle = 0.0
        var last = System.currentTimeMillis()

        registerHandler<GameLoop> {
            val computed = angle / 20.0
            if (System.currentTimeMillis() - last > 200) {
                last = System.currentTimeMillis()
            }
        }


        registerHandler<RenderPass> {
            if (inited) {
                angle = if (angle > 7200) 0.0 else angle + 1

                val player = clientApi.minecraft().player

                // GL начало
                GlStateManager.disableLighting()
                GlStateManager.disableTexture2D()
                GlStateManager.disableAlpha()
                GlStateManager.disableCull()
                GlStateManager.shadeModel(GL11.GL_SMOOTH)
                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR)
                GlStateManager.color(
                    255f,
                    255f,
                    255f,
                    0.6f
                )

                GlStateManager.translate(-player.x, -player.y, -player.z)
                TowerManager.activeAmmo.forEach { it.draw() }
                GlStateManager.translate(cube.x, cube.y, cube.z)
                GlStateManager.rotate(
                    (angle / 23.0f).toFloat(),
                    cos(angle / 31.0).toFloat(),
                    sin(angle / 47.0).toFloat(),
                    sin(angle / 51.0).toFloat()
                )

                GL11.glBegin(GL_POLYGON)
                GL11.glColor3d(1.0, 1.0, 1.0)
                GL11.glVertex3d(0.5, -0.5, 0.5)
                GL11.glVertex3d(0.5, 0.5, 0.5)
                GL11.glVertex3d(-0.5, 0.5, 0.5)
                GL11.glVertex3d(-0.5, -0.5, 0.5)
                GL11.glEnd()
                GL11.glBegin(GL_POLYGON)
                GL11.glColor3d(1.0, 0.0, 1.0)
                GL11.glVertex3d(0.5, -0.5, -0.5)
                GL11.glVertex3d(0.5, 0.5, -0.5)
                GL11.glVertex3d(0.5, 0.5, 0.5)
                GL11.glVertex3d(0.5, -0.5, 0.5)
                GL11.glEnd()
                GL11.glBegin(GL_POLYGON)
                GL11.glColor3d(0.0, 1.0, 0.0)
                GL11.glVertex3d(-0.5, -0.5, 0.5)
                GL11.glVertex3d(-0.5, 0.5, 0.5)
                GL11.glVertex3d(-0.5, 0.5, -0.5)
                GL11.glVertex3d(-0.5, -0.5, -0.5)
                GL11.glEnd()
                GL11.glBegin(GL_POLYGON)
                GL11.glColor3d(0.0, 0.0, 1.0)
                GL11.glVertex3d(0.5, 0.5, 0.5)
                GL11.glVertex3d(0.5, 0.5, -0.5)
                GL11.glVertex3d(-0.5, 0.5, -0.5)
                GL11.glVertex3d(-0.5, 0.5, 0.5)
                GL11.glEnd()
                GL11.glBegin(GL_POLYGON)
                GL11.glColor3d(1.0, 0.0, 0.0)
                GL11.glVertex3d(0.5, -0.5, -0.5)
                GL11.glVertex3d(0.5, -0.5, 0.5)
                GL11.glVertex3d(-0.5, -0.5, 0.5)
                GL11.glVertex3d(-0.5, -0.5, -0.5)
                GL11.glEnd()

                // GL конец
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR)
                GlStateManager.shadeModel(GL11.GL_FLAT)
                GlStateManager.enableTexture2D()
                GlStateManager.enableAlpha()
                GlStateManager.enableCull()
            }
        }
    }
}