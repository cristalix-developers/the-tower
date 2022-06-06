import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

object TimeBar {

    var bar: RectangleElement? = null
    private lateinit var line: RectangleElement
    private lateinit var content: TextElement

    init {
        var time = 0
        var currentTime = System.currentTimeMillis()

        mod.registerHandler<GameLoop> {
            if (System.currentTimeMillis() - currentTime > 1000) {
                time--
                currentTime = System.currentTimeMillis()
                content.content = content.content.dropLast(7) + (time / 60).toString()
                    .padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0') + " ⏳"
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
                size = V3(0.0, 5.0, 0.0)
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
            line.size.x = 180.0
            bar!!.enabled = true
            content.content = text

            line.animate(time - 0.1) { size.x = 0.0 }

            UIEngine.schedule(time) { UIEngine.overlayContext.removeChild(bar!!) }
        }
    }
}