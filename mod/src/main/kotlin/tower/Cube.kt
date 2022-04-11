package tower

import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import mod
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.JavaMod.clientApi
import kotlin.math.cos
import kotlin.math.sin

/**
 * @project tower
 * @author Рейдж
 */
object Cube {

    init {
        var angle = 0.0

        mod.registerHandler<RenderPass> {
            if (mod.inited) {
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

                clientApi.renderEngine()
                    .bindTexture(ResourceLocation.of("minecraft", "DIAMOND_BLOCK"))

                GlStateManager.translate(-player.x, -player.y, -player.z)
                GlStateManager.translate(mod.cube.x, mod.cube.y, mod.cube.z)
                GlStateManager.rotate(
                    (angle / 23.0f).toFloat(),
                    cos(angle / 31.0).toFloat(),
                    sin(angle / 47.0).toFloat(),
                    sin(angle / 51.0).toFloat()
                )

                GL11.glBegin(GL11.GL_POLYGON)
                GL11.glColor3d(1.0, 1.0, 1.0)
                GL11.glVertex3d(1.0, -1.0, 1.0)
                GL11.glVertex3d(1.0, 1.0, 1.0)
                GL11.glVertex3d(-1.0, 1.0, 1.0)
                GL11.glVertex3d(-1.0, -1.0, 1.0)
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
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR)
                GlStateManager.shadeModel(GL11.GL_FLAT)
                GlStateManager.enableTexture2D()
                GlStateManager.enableAlpha()
                GlStateManager.enableCull()
            }
        }
    }
}