import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.max
import kotlin.math.min

/**
 * @project tower
 * @author Рейдж
 */
object BarManager {

    private var healthIndicator: HealthIndicator? = null
    private var protectionIndicator: ProtectionIndicator? = null
    private var lvlIndicator: LevelIndicator? = null
    private var airBar: RectangleElement? = null

    private var health = 5
    private var maxHealth = 5
    private var protection = .1
    private var exp = 0
    private var level = 0

    private var airHide: Boolean = false

    init {
        mod.registerChannel("tower:loseheart") {
            health = readInt()
            maxHealth = readInt()
        }

        mod.registerChannel("tower:protection") {
            val protect = readDouble()
            protection = protect

            if (protect != protection) {
                protection = protect
                protectionIndicator?.updatePercentage(protect)
            }
        }

        mod.registerChannel("tower:exp") {
            val actualLevel = readInt()
            val haveExp = readInt()
            val needExp = readInt()

            if (actualLevel != level || haveExp != exp) {
                exp = haveExp
                level = actualLevel
                lvlIndicator?.updatePercentage(level, exp, needExp)
            }
        }

        mod.registerChannel("tower:barvisible") {
            healthIndicator!!.enabled = !healthIndicator!!.enabled
            protectionIndicator!!.enabled = !protectionIndicator!!.enabled
        }

        registerHandler<RenderTickPre> {
            if (airBar != null) {
                var air = JavaMod.clientApi.minecraft().player.air
                air = max(0, air)

                if (!airHide)
                    airBar!!.children[0].size.x = air.toDouble() / 300 * 260.0

                if (air == 300 && !airHide) {
                    airHide = true
                    UIEngine.overlayContext.removeChild(airBar!!)
                }

                if (air < 300 && airHide) {
                    airHide = false
                    UIEngine.overlayContext.addChild(airBar!!)
                }
            }
            healthIndicator?.updatePercentage(health, maxHealth)
        }
        display()
    }

    private fun display() {
        healthIndicator = HealthIndicator()
        protectionIndicator = ProtectionIndicator()
        lvlIndicator = LevelIndicator()

        healthIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "health_bar.png")
        protectionIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "energy.png")
        lvlIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "xp_bar.png")

        val parent = rectangle {
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            offset.y = -14.0
        }
        parent.addChild(healthIndicator!!, protectionIndicator!!, lvlIndicator!!)

        UIEngine.overlayContext.addChild(parent)

        healthIndicator!!.updatePercentage(health, maxHealth)
        protectionIndicator!!.updatePercentage(protection)
        lvlIndicator!!.updatePercentage(level, exp, 0)

        airBar = rectangle {
            size = V3(260.0, 3.0)
            color.alpha = 0.68
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            offset.y = -51.0
            addChild(rectangle {
                size = V3(260.0, 3.0)
                color = WHITE
            })
        }
        UIEngine.overlayContext.addChild(airBar!!)
    }

    class HealthIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double

        init {
            enabled = false
            color = Color(0, 0, 0, 0.68)
            offset = V3(-1.0, -30.0)
            align = Relative.CENTER
            origin = Relative.RIGHT
            size = V3(99.0, 10.0)

            val parentSize = size
            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage(current: Int, max: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, current / max.toDouble())
            }
            this.text.content = "§f$current/$max ❤"
        }
    }

    class ProtectionIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double

        init {
            enabled = false
            color = Color(0, 0, 0, 0.68)
            offset = V3(1.0, -30.0)
            align = Relative.CENTER
            origin = Relative.LEFT
            size = V3(99.0, 10.0)

            val parentSize = size

            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage(energy: Double) {
            this.text.content = "§f$energy 㱈"
        }
    }

    class LevelIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset.y = -18.0
            align = Relative.CENTER
            origin = Relative.CENTER
            size = V3(200.0, 10.0)

            val parentSize = size
            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage(level: Int, exp: Int, needExp: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, exp / needExp.toDouble())
            }
            this.text.content = "§f$level ур. §b$exp/$needExp"
        }
    }
}