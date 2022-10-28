import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.resource.ResourceLocation
import mob.MobManager
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

class TimeBar {

    private val box = carved {
        origin = TOP
        align = TOP
        color = Color(0, 0, 0, 0.62)
        size = V3(60.0, 20.0)
        +text {
            origin = CENTER
            align = CENTER
            shadow = true
            offset.x += 5.0
            +rectangle {
                origin = LEFT
                align = LEFT
                color = WHITE
                size = V3(12.0, 12.0)
                offset = V3(8.0, 0.5)
                textureLocation = ResourceLocation.of("minecraft", "mcpatcher/cit/tower/watch.png")
            }
        }
    }
    private val content = box.children[4] as TextElement

    init {
        var time = 0
        var currentTime = System.currentTimeMillis()

        mod.registerHandler<GameLoop> {
            if (System.currentTimeMillis() - currentTime > 1000) {
                time--
                currentTime = System.currentTimeMillis()
                content.content =
                    (time / 60).toString().padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0')
                if (MobManager.mobs.isEmpty()) {
                    UIEngine.overlayContext.removeChild(box)
                    UIEngine
                }
            }
        }

        mod.registerChannel("tower:bar") {
            time = readInt()

            if (time == 0) {
                UIEngine.overlayContext.removeChild(box)
                return@registerChannel
            }

            content.content = (time / 60).toString().padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0')
            UIEngine.overlayContext + box
        }
    }
}