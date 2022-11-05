
import dev.xdark.clientapi.event.lifecycle.GameLoop
import mob.MobManager
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

class TimeBar {


    private val content = text {
        origin = TOP
        align = TOP
        color = WHITE
        shadow = true
        content = "Загрузка..."
        offset.y -= 15
    }

    private val line = carved {
        origin = LEFT
        align = LEFT
        size = V3(180.0, 5.0, 0.0)
        color = Color(42, 102, 189, 1.0)
    }

    private val cooldown = carved {
        offset.y += 30
        origin = TOP
        align = TOP
        size = V3(180.0, 5.0, 0.0)
        color = Color(0, 0, 0, 0.62)
        +line
        +content
    }

    init {
        var time = 0
        var currentTime = System.currentTimeMillis()

        mod.registerHandler<GameLoop> {
            if (System.currentTimeMillis() - currentTime > 1000) {
                time--
                currentTime = System.currentTimeMillis()
                content.content = content.content.dropLast(7) + (time / 60).toString()
                        .padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0') + " ⏳"
                if (MobManager.mobs.isEmpty()) {
                    UIEngine.overlayContext.removeChild(cooldown)
                }
            }
        }

        mod.registerChannel("tower:bar") {
            val text = readUtf8() + " XX:XX ⏳"
            time = readInt()

            if (time == 0) {
                UIEngine.overlayContext.removeChild(cooldown)
                return@registerChannel
            }

            content.content = text
            UIEngine.overlayContext + cooldown
        }
    }
}