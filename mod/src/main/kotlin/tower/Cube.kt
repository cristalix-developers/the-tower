package tower

import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import mob.MobManager
import mod
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.JavaMod.clientApi
import tower.TowerManager.radius
import kotlin.math.cos
import kotlin.math.sin

/**
 * @project tower
 * @author Рейдж
 */
object Cube {

    init {
        var angle = 0.0
        val mc = clientApi.minecraft()
        val distanceBetweenTowerAndGround = 9.0

        var lastRender = System.currentTimeMillis()

        mod.registerHandler<RenderPass> {
            if (mod.inited) {
                val entity = mc.renderViewEntity

                val pt = mc.timer.renderPartialTicks
                val prevX = entity.prevX
                val prevY = entity.prevY
                val prevZ = entity.prevZ

                val now = System.currentTimeMillis()
                if (now - lastRender > 5) {
                    angle = if (angle > 7200) 0.0 else angle + 1
                    lastRender = now
                }

                // GL начало
                GlStateManager.disableLighting()
                GlStateManager.disableTexture2D()
                GlStateManager.disableAlpha()
                GlStateManager.disableCull()
                GlStateManager.shadeModel(GL11.GL_SMOOTH)
                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

                clientApi.renderEngine()
                    .bindTexture(ResourceLocation.of("minecraft", "textures/blocks/diamond_block.png"))

                GlStateManager.translate(
                    -(entity.x - prevX) * pt - prevX,
                    -(entity.y - prevY) * pt - prevY,
                    -(entity.z - prevZ) * pt - prevZ,
                )
                GlStateManager.translate(mod.cube.x, mod.cube.y - distanceBetweenTowerAndGround, mod.cube.z)

                // Рисуем выделение зоны
                GlStateManager.color(0.066666f, 0.52941f, 1f, 0.21f)
                GL11.glBegin(GL11.GL_POLYGON)
                val angles = 40.0
                val radius = TowerManager.radius
                repeat(angles.toInt()) {
                    GL11.glVertex3d(
                        radius * sin(Math.toRadians(it / angles * 360.0)),
                        0.01,
                        radius * cos(Math.toRadians(it / angles * 360.0))
                    )
                }
                GL11.glEnd()

                // Рисуем выделение зоны
                GlStateManager.color(0.066666f, 0.52941f, 1f, 1f)
                GlStateManager.glLineWidth(5f)
                GL11.glBegin(GL11.GL_LINE_LOOP)
                repeat(angles.toInt()) {
                    GL11.glVertex3d(
                        radius * sin(Math.toRadians(it / angles * 360.0)),
                        0.01,
                        radius * cos(Math.toRadians(it / angles * 360.0))
                    )
                }
                GL11.glEnd()

                GlStateManager.translate(0.0, distanceBetweenTowerAndGround, 0.0)

                GlStateManager.color(
                    1.0f,
                    1.0f,
                    1.0f,
                    0.6f
                )

                GlStateManager.rotate(
                    (angle / 23.0f).toFloat(),
                    cos(angle / 31.0).toFloat(),
                    sin(angle / 47.0).toFloat(),
                    sin(angle / 51.0).toFloat()
                )

                GL11.glBegin(GL11.GL_POLYGON)
                GL11.glColor3d(1.0, 1.0, 1.0)
                GL11.glVertex3d(1.0, -1.0, 1.0)
                GL11.glTexCoord2d(0.0, 0.0)
                GL11.glVertex3d(1.0, 1.0, 1.0)
                GL11.glTexCoord2d(0.0, 1.0)
                GL11.glVertex3d(-1.0, 1.0, 1.0)
                GL11.glTexCoord2d(1.0, 1.0)
                GL11.glVertex3d(-1.0, -1.0, 1.0)
                GL11.glTexCoord2d(1.0, 0.0)
                GL11.glEnd()
                GL11.glBegin(GL11.GL_POLYGON)
                GL11.glColor3d(1.0, 0.0, 1.0)
                GL11.glVertex3d(1.0, -1.0, -1.0)
                GL11.glVertex3d(1.0, 1.0, -1.0)
                GL11.glVertex3d(1.0, 1.0, 1.0)
                GL11.glVertex3d(1.0, -1.0, 1.0)
                GL11.glEnd()
                GL11.glBegin(GL11.GL_POLYGON)
                GL11.glColor3d(1.0, 1.0, 0.0)
                GL11.glVertex3d(-1.0, -1.0, 1.0)
                GL11.glVertex3d(-1.0, 1.0, 1.0)
                GL11.glVertex3d(-1.0, 1.0, -1.0)
                GL11.glVertex3d(-1.0, -1.0, -1.0)
                GL11.glEnd()
                GL11.glBegin(GL11.GL_POLYGON)
                GL11.glColor3d(0.0, 0.0, 1.0)
                GL11.glVertex3d(1.0, 1.0, 1.0)
                GL11.glVertex3d(1.0, 1.0, -1.0)
                GL11.glVertex3d(-1.0, 1.0, -1.0)
                GL11.glVertex3d(-1.0, 1.0, 1.0)
                GL11.glEnd()
                GL11.glBegin(GL11.GL_POLYGON)
                GL11.glColor3d(1.0, 0.0, 0.0)
                GL11.glVertex3d(1.0, -1.0, -1.0)
                GL11.glVertex3d(1.0, -1.0, 1.0)
                GL11.glVertex3d(-1.0, -1.0, 1.0)
                GL11.glVertex3d(-1.0, -1.0, -1.0)
                GL11.glEnd()

                // GL конец
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.shadeModel(GL11.GL_FLAT)
                GlStateManager.enableTexture2D()
                GlStateManager.enableAlpha()
                GlStateManager.enableCull()
            }
        }
    }
}