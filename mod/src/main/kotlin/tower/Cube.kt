package tower

import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import mod
import org.lwjgl.opengl.GL11.*
import ru.cristalix.clientapi.JavaMod.clientApi
import java.lang.Math.cos
import java.lang.Math.sin

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
                GlStateManager.disableAlpha()
                GlStateManager.disableCull()
                GlStateManager.shadeModel(GL_SMOOTH)
                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                GlStateManager.translate(
                    -(entity.x - prevX) * pt - prevX,
                    -(entity.y - prevY) * pt - prevY,
                    -(entity.z - prevZ) * pt - prevZ,
                )
                GlStateManager.translate(mod.cube.x, mod.cube.y - distanceBetweenTowerAndGround, mod.cube.z)

                clientApi.renderEngine()
                    .bindTexture(ResourceLocation.of("minecraft", "textures/blocks/diamond_block.png"))

                // Рисуем выделение зоны
                GlStateManager.color(0.066666f, 0.52941f, 1f, 0.21f)
                glBegin(GL_POLYGON)
                val angles = 60.0
                val radius = TowerManager.radius
                repeat(angles.toInt()) {
                    glVertex3d(
                        radius * sin(Math.toRadians(it / angles * 360.0)),
                        0.01,
                        radius * cos(Math.toRadians(it / angles * 360.0))
                    )
                }
                glEnd()

                // Рисуем выделение зоны
                GlStateManager.color(0.066666f, 0.52941f, 1f, 1f)
                GlStateManager.glLineWidth(5f)
                glBegin(GL_LINE_LOOP)
                repeat(angles.toInt()) {
                    glVertex3d(
                        radius * sin(Math.toRadians(it / angles * 360.0)),
                        0.01,
                        radius * cos(Math.toRadians(it / angles * 360.0))
                    )
                }
                glEnd()

                GlStateManager.translate(0.0, distanceBetweenTowerAndGround, 0.0)

                GlStateManager.color(
                    1.0f,
                    1.0f,
                    1.0f,
                    1.0f
                )

                GlStateManager.rotate(
                    (angle / 23.0f).toFloat(),
                    cos(angle / 31.0).toFloat(),
                    sin(angle / 47.0).toFloat(),
                    sin(angle / 51.0).toFloat()
                )

                glScalef(0.8f, 0.8f, 0.8f)

                glBegin(GL_POLYGON)
                glVertex3d(1.0, -1.0, -1.0)
                glTexCoord2d(0.0, 0.0)
                glVertex3d(1.0, 1.0, -1.0)
                glTexCoord2d(1.0, 1.0)
                glVertex3d(1.0, 1.0, 1.0)
                glTexCoord2d(0.0, 1.0)
                glVertex3d(1.0, -1.0, 1.0)
                glTexCoord2d(1.0, 0.0)
                glEnd()
                glBegin(GL_POLYGON)
                glVertex3d(-1.0, -1.0, 1.0)
                glTexCoord2d(0.0, 0.0)
                glVertex3d(-1.0, 1.0, 1.0)
                glTexCoord2d(0.0, 1.0)
                glVertex3d(-1.0, 1.0, -1.0)
                glTexCoord2d(1.0, 1.0)
                glVertex3d(-1.0, -1.0, -1.0)
                glTexCoord2d(1.0, 0.0)
                glEnd()
                glBegin(GL_POLYGON)
                glVertex3d(1.0, 1.0, 1.0)
                glTexCoord2d(0.0, 0.0)
                glVertex3d(1.0, 1.0, -1.0)
                glTexCoord2d(0.0, 1.0)
                glVertex3d(-1.0, 1.0, -1.0)
                glTexCoord2d(1.0, 1.0)
                glVertex3d(-1.0, 1.0, 1.0)
                glTexCoord2d(1.0, 0.0)
                glEnd()
                glBegin(GL_POLYGON)
                glVertex3d(1.0, -1.0, -1.0)
                glTexCoord2d(0.0, 0.0)
                glVertex3d(1.0, -1.0, 1.0)
                glTexCoord2d(0.0, 1.0)
                glVertex3d(-1.0, -1.0, 1.0)
                glTexCoord2d(1.0, 1.0)
                glVertex3d(-1.0, -1.0, -1.0)
                glTexCoord2d(1.0, 0.0)
                glEnd()
                glBegin(GL_POLYGON)
                glVertex3d(-1.0, -1.0, -1.0)
                glTexCoord2d(0.0, 0.0)
                glVertex3d(-1.0, 1.0, -1.0)
                glTexCoord2d(0.0, 1.0)
                glVertex3d(1.0, 1.0, -1.0)
                glTexCoord2d(1.0, 1.0)
                glVertex3d(1.0, -1.0, -1.0)
                glTexCoord2d(1.0, 0.0)
                glEnd()
                glBegin(GL_POLYGON)
                glVertex3d(1.0, -1.0, 1.0)
                glTexCoord2d(0.0, 0.0)
                glVertex3d(1.0, 1.0, 1.0)
                glTexCoord2d(0.0, 1.0)
                glVertex3d(-1.0, 1.0, 1.0)
                glTexCoord2d(1.0, 1.0)
                glVertex3d(-1.0, -1.0, 1.0)
                glTexCoord2d(1.0, 0.0)
                glEnd()

                // GL конец
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.shadeModel(GL_FLAT)
                GlStateManager.enableAlpha()
                GlStateManager.enableCull()
            }
        }
    }
}