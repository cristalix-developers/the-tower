package tower

import NAMESPACE
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import tower.TowerManager.health
import tower.TowerManager.maxHealth
import tower.TowerManager.protection
import util.Formatter
import kotlin.math.min

/**
 * @project tower
 * @author Рейдж
 */
object BarManager {

    var healthIndicator: HealthIndicator? = null
    var protectionIndicator: ProtectionIndicator? = null

    private val parent = rectangle {
        origin = Relative.BOTTOM
        align = Relative.BOTTOM
        offset.y = -14.0
    }
    init {
        healthIndicator = HealthIndicator()
        protectionIndicator = ProtectionIndicator()

        healthIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "health_bar.png")
        protectionIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "energy.png")

        parent.addChild(healthIndicator!!, protectionIndicator!!)

        UIEngine.overlayContext.addChild(parent)

        healthIndicator!!.updatePercentage(health, maxHealth)
        protectionIndicator!!.updatePercentage(protection)
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
            offset = V3(-1.0, -15.0)
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
            offset = V3(1.0, -15.0)
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
}