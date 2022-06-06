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
import util.Formatter
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

    private var exp = 0
    private var level = 0
    init {
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
            this.text.content = "§f${Formatter.toFormat(current)}/${Formatter.toFormat(max)} ❤"
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
            this.text.content = "§f${Formatter.toFormat(protection)} 㱈"
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