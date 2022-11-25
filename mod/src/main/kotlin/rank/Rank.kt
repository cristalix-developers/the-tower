package rank

import NAMESPACE
import dev.xdark.clientapi.entity.Entity
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import ru.cristalix.clientapi.KotlinModHolder
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.*
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

const val ENTITY_SIZE = 30

class Rank {

    init {
        val ranks = hashMapOf<UUID, Context3D>()
        val playerBuffTextures = hashMapOf<UUID, ResourceLocation>()
        val activeEntities = mutableListOf<Entity>()

        val minecraft = UIEngine.clientApi.minecraft()
        val playerUUID = minecraft.player.uniqueID
        val context = Context3D(V3())

        val body = rectangle {
            align = TOP
            origin = TOP
            size = V3(15.0, 15.0)
            color = Color(255, 255, 255)
            context.rotation = Rotation(0.0, 0.0, 1.0, 0.0)
            rotation = Rotation(0.0, 1.0, 0.0, 0.0)
        }

        context.addChild(body)

        KotlinModHolder.mod.registerHandler<RenderTickPre> {
            val player = UIEngine.clientApi.minecraft().player
            val matrix = Matrix4f()
            Matrix4f.setIdentity(matrix)
            Matrix4f.rotate(
                ((player.rotationYaw + 180) / 180 * Math.PI).toFloat(),
                Vector3f(0f, -1f, 0f),
                matrix,
                matrix
            )
            Matrix4f.rotate((player.rotationPitch / 180 * Math.PI).toFloat(), Vector3f(-1f, 0f, 0f), matrix, matrix)
            ranks.forEach { it.value.matrices[rotationMatrix] = matrix }
        }

        KotlinModHolder.mod.registerHandler<NameTemplateRender> {
            if (entity !is Entity) return@registerHandler
            val entity = entity as Entity
            val partialTicks = UIEngine.clientApi.minecraft().timer.renderPartialTicks

            if (entity.uniqueID in playerBuffTextures) {
                if (activeEntities.size > ENTITY_SIZE) {
                    activeEntities.clear()
                }
                activeEntities.add(entity)
            }

            ranks.values.forEach {
                activeEntities.forEach { entity ->
                    it.offset = V3(
                        entity.lastX + (entity.x - entity.lastX) * partialTicks,
                        entity.lastY + (entity.y - entity.lastY) * partialTicks + 3.3,
                        entity.lastZ + (entity.z - entity.lastZ) * partialTicks
                    )
                    GlStateManager.disableLighting()
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    GL11.glDepthMask(false)

                    it.transformAndRender()
                    GlStateManager.enableLighting()
                    GL11.glDepthMask(true)
                }
            }
        }

        KotlinModHolder.mod.registerChannel("tower:rank") {
            val uuid = readId()
            val texture = NetUtil.readUtf8(this)

            if (playerUUID == uuid) {
                return@registerChannel
            }

            body.textureLocation = UIEngine.clientApi.resourceManager().getLocation(NAMESPACE, texture)

            ranks[uuid] = context
            playerBuffTextures[uuid] = ResourceLocation.of(NAMESPACE, texture)
        }

        KotlinModHolder.mod.registerChannel("tower:rank-remove") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            ranks[uuid]?.let {
                UIEngine.worldContexts.remove(it)
                ranks.remove(uuid)
                activeEntities.clear()
            }
        }
    }
}