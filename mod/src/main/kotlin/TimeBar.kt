import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import mob.MobManager
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.EventLoopImpl
import ru.cristalix.uiengine.eventloop.Task
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.reflect.KMutableProperty

object TimeBar {

    private lateinit var line: RectangleElement
    private lateinit var content: TextElement

    var isRemove = false

    init {
        var currentTime = System.currentTimeMillis()
        var bar: RectangleElement? = null
        var time = 0

        mod.registerHandler<GameLoop> {
            if (System.currentTimeMillis() - currentTime > 1000) {
                time--
                currentTime = System.currentTimeMillis()
                content.content = content.content.dropLast(7) + (time / 60).toString()
                    .padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0') + " ⏳"
                if (MobManager.mobs.isEmpty() || !mod.gameActive) {
                    isRemove = true
                    UIEngine.overlayContext.removeChild(bar!!)
                }
            }
        }

        content = text {
            origin = TOP
            align = TOP
            color = WHITE
            shadow = true
            content = "Загрузка..."
            offset.y -= 15
        }

        mod.registerChannel("tower:timebar") {
            val text = NetUtil.readUtf8(this) + " XX:XX ⏳"
            time = this.readInt()

            line = rectangle {
                origin = LEFT
                align = LEFT
                size = V3(180.0, 5.0, 0.0)
                color = Color(42, 102, 189, 1.0)
            }

            bar = rectangle {
                enabled = false
                offset = V3(0.0, 25.0)
                origin = TOP
                align = TOP
                size = V3(180.0, 5.0, 0.0)
                color = Color(0, 0, 0, 0.62)
                +line
                +content
            }

            if (time == 0) {
                UIEngine.overlayContext.removeChild(bar!!)
                return@registerChannel
            }

            UIEngine.overlayContext + bar!!
            content.content = text
            bar!!.enabled = true
            isRemove = false

            line.animate(time - 0.1) { size.x = 0.0 }

            UIEngine.schedule(time) {
                if (isRemove)
                    UIEngine.overlayContext.removeChild(bar!!)
            }
        }
    }
}