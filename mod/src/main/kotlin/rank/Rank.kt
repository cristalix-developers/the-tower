package rank

import NAMESPACE
import dev.xdark.clientapi.entity.Entity
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

const val ENTITY_SIZE = 30
const val VIEW_DISTANCE = 30

class Rank {

    private val ranks = mutableMapOf<UUID, Context3D>()
    private val playerBuffTextures: MutableMap<UUID, ResourceLocation> = mutableMapOf()
    private val activeEntities = mutableListOf<Entity>()

    private val minecraft = UIEngine.clientApi.minecraft()
    private val playerUUID = minecraft.player.uniqueID

    init {
        mod.registerChannel("tower:rank") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val texture = NetUtil.readUtf8(this)
            val x = readDouble()
            val y = readDouble() + 3.3
            val z = readDouble()

            if (playerUUID == uuid)
                return@registerChannel

            val context = Context3D(V3(x, y, z))

            context.addChild(rectangle {
                align = TOP
                origin = TOP
                size = V3(15.0, 15.0)
                color = Color(255, 255, 255)
                textureLocation = UIEngine.clientApi.resourceManager().getLocation(NAMESPACE, texture)
                context.rotation = Rotation(0.0, 0.0, 1.0, 0.0)
                rotation = Rotation(0.0, 1.0, 0.0, 0.0)
            })

            ranks[uuid] = context
            playerBuffTextures[uuid] = ResourceLocation.of(NAMESPACE, texture)

            UIEngine.worldContexts.add(context)
        }

        mod.registerChannel("tower:rank-remove") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            ranks[uuid]?.let {
                UIEngine.worldContexts.remove(it)
                ranks.remove(uuid)
                activeEntities.clear()
            }
        }

        mod.registerHandler<NameTemplateRender> {
            if (entity !is Entity)
                return@registerHandler
            val entity = entity as Entity
            if (!activeEntities.contains(entity) && playerBuffTextures.containsKey(entity.uniqueID)) {
                if (activeEntities.size > ENTITY_SIZE)
                    activeEntities.clear()
                activeEntities.add(entity)
            }
        }

        mod.registerHandler<RenderTickPre> {
            val player = minecraft.player
            val timer = minecraft.timer
            val yaw =
                (player.rotationYaw - player.prevRotationYaw) * timer.renderPartialTicks + player.prevRotationYaw
            val pitch =
                (player.rotationPitch - player.prevRotationPitch) * timer.renderPartialTicks + player.prevRotationPitch

            ranks.values.forEach {
                it.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                it.children[0].rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0)

                if (activeEntities.isEmpty())
                    return@registerHandler

                activeEntities.forEach { entity ->
                    if (sqrt(
                            (player.x - entity.x).pow(2.0) + (player.x - entity.x).pow(2.0) + (player.z - entity.z).pow(
                                2.0
                            )
                        ) <= VIEW_DISTANCE
                    ) {
                        if (!it.enabled) {
                            it.enabled = true
                            it.children[0].enabled = true
                        }
                        it.animate(0.01) {
                            offset.x = entity.x
                            offset.y = entity.y + 3.3
                            offset.z = entity.z
                        }
                    } else {
                        it.enabled = false
                        it.children[0].enabled = false
                    }
                }
            }
        }
    }
}