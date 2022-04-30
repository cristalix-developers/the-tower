import dev.xdark.clientapi.entity.EntityLiving
import dev.xdark.clientapi.event.entity.EntityLeftClick
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import gui.UpgradeGui
import io.netty.buffer.Unpooled
import mob.MobManager
import org.lwjgl.opengl.GL11.*
import player.Statistic
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import tower.BarManager
import tower.Cube
import tower.TowerManager
import java.util.*


lateinit var mod: App
const val NAMESPACE = "tower"
const val FILE_STORE = "http://storage.c7x.ru/reidj/"

class App : KotlinMod() {

    lateinit var cube: V3
    var inited = false
    var gameActive = false

    val playerBuffTextures: MutableMap<UUID, ResourceLocation> = mutableMapOf()
    val activeEntities = mutableListOf<EntityLiving>()

    override fun onEnable() {
        mod = this
        UIEngine.initialize(this)

        Statistic
        TimeBar
        Ending
        GlowEffect
        ItemTitle
        UpgradeGui

        loadTextures(
            load("health_bar.png", "35320C088F83D8890128127"),
            load("energy.png", "35320C088F83D8890128111"),
            load("xp_bar.png", "35320C094F83D8890128111"),
            load("block.png", "35320C023F83D8890128111"),
            load("tower/krug.png", "94420C323F83D8890128111"),
            load("tower/krug5.png", "92520C323F95D8890128111"),
            load("tower/2.png", "92520C323F95D8890127811")
        ).thenRun {
            BarManager
            TowerManager
            Cube

            playerBuffTextures[UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35fd")] =
                ResourceLocation.of(NAMESPACE, "2.png")

            registerHandler<NameTemplateRender> a@{
                if (entity !is EntityLiving) return@a
                val entity = entity as EntityLiving

                if (!activeEntities.contains(entity) && playerBuffTextures.containsKey(entity.uniqueID)) {
                    if (activeEntities.size > 30)
                        activeEntities.clear()
                    activeEntities.add(entity)
                }
            }

            val scale = 1.5
            val player = clientApi.minecraft().player

            registerHandler<RenderPass> {
                GlStateManager.disableLighting()
                GlStateManager.disableAlpha()
                GlStateManager.disableCull()
                GlStateManager.shadeModel(GL_SMOOTH)
                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

                activeEntities.forEach {
                    clientApi.renderEngine().bindTexture(playerBuffTextures[it.uniqueID])

                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP)

                    GlStateManager.translate(-player.x, -player.y, -player.z)
                    GlStateManager.translate(it.x, it.y, it.z)

                    glBegin(GL_QUADS)

                    glTexCoord2d(0.0, 0.0)
                    glVertex3d(-0.5 * scale, 0.01, -0.5 * scale)
                    glTexCoord2d(1.0, 0.0)
                    glVertex3d(0.5 * scale, 0.01, -0.5 * scale)
                    glTexCoord2d(1.0, 1.0)
                    glVertex3d(0.5 * scale, 0.01, 0.5 * scale)
                    glTexCoord2d(0.0, 1.0)
                    glVertex3d(-0.5 * scale, 0.01, 0.5 * scale)

                    glEnd()
                }

                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.shadeModel(GL_FLAT)
                GlStateManager.enableAlpha()
                GlStateManager.enableCull()
            }
        }

/*val item = Block.Builder.builder()
.material(
Material.Builder.builder()
.liquid(false)
.blocksLight(false)
.blocksMovement(true)
.translucent(false)
.solid(true)
.pushReaction(PushReaction.IGNORE)
.build()
)
.translationKey("block")
.blockHardnessHandler { _, _, _, _, _ -> 1.5F }
.baseHardness(1.5F)
.slipperiness(0.98F)
.creativeTab(CreativeTab.COMBAT)
.blockRenderLayer(BlockRenderLayer.SOLID)
.solidTopHandler { true }
.fullBlockHandler { true }
.opaqueCubeHandler { true }
.fullCubeHandler { true }
.soundType(SoundType.SAND)
.build()

clientApi.blockRegistry().register(
274,
ResourceLocation.of("minecraft", "block"),
item
)

clientApi.renderItem().registerBlock(item, "minecraft/textures/blocks/block")*/

        registerHandler<HealthRender> { isCancelled = true }
        registerHandler<ExpBarRender> { isCancelled = true }
        registerHandler<HungerRender> { isCancelled = true }
        registerHandler<ArmorRender> { isCancelled = true }
        registerHandler<AirBarRender> { isCancelled = true }
        registerHandler<VehicleHealthRender> { isCancelled = true }

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
                Unpooled.copiedBuffer(entity.uniqueID.toString(), Charsets.UTF_8)
            )
        }
    }

    private fun load(path: String, hash: String): RemoteTexture {
        return RemoteTexture(ResourceLocation.of(NAMESPACE, path), hash)
    }
}