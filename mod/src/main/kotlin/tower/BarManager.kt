package tower

import NAMESPACE
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import mod
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import tower.TowerManager.health
import tower.TowerManager.maxHealth
import tower.TowerManager.protection
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

/**
 * @project tower
 * @author Рейдж
 */
object BarManager {

    var healthIndicator: HealthIndicator? = null
    var protectionIndicator: ProtectionIndicator? = null
    private var lvlIndicator: LevelIndicator? = null
    private var airBar: RectangleElement? = null

    private var exp = 0
    private var level = 0

    private var airHide: Boolean = false

    private val MONEY_FORMAT = DecimalFormat("##.#")

    fun toMoneyFormat(health: Double): String = MONEY_FORMAT.format(health)

    init {
        registerHandler<GameLoop> {
            healthIndicator!!.enabled = mod.gameActive
            protectionIndicator!!.enabled = mod.gameActive
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

        fun updatePercentage(current: Double, max: Double) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, current / max)
            }
            this.text.content = "§f${toMoneyFormat(current)}/${toMoneyFormat(max)} ❤"
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

        fun updatePercentage(protection: Double) {
            this.text.content = "§f${toMoneyFormat(protection)} 㱈"
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
            enabled = false
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