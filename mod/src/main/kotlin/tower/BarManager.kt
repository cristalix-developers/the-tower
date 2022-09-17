package tower

import dev.xdark.clientapi.resource.ResourceLocation
import mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*
import util.Formatter

/**
 * @project tower
 * @author Рейдж
 */
object BarManager {

    val tokenBox = carved {
        origin = BOTTOM
        align = BOTTOM
        offset = V3(-62.5, -24.0)
        size = V3(57.0, 12.0)
        color = Color(0, 0, 0, 0.62)
        enabled = false
        +rectangle {
            origin = CENTER
            align = LEFT
            size = V3(9.5, 9.5, 9.5)
            color = WHITE
            offset.x += 8.0
            textureLocation = ResourceLocation.of("minecraft", "mcpatcher/cit/tower/token.png")
        }
        +text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(0.95, 0.95, 0.95)
            offset.x += 4.0
            content = "Загрузка..."
        }
    }
    val healthBox = carved {
        origin = BOTTOM
        align = BOTTOM
        offset = V3(5.8, -24.0)
        size = V3(77.5, 12.0)
        color = Color(160,29,40, 1.0)
        enabled = false
        +text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(0.95, 0.95, 0.95)
            content = "5 из 5"
        }
        +rectangle {
            origin = CENTER
            align = LEFT
            size = V3(9.5, 9.5, 9.5)
            color = WHITE
            offset.x += 10.0
            textureLocation = ResourceLocation.of("minecraft", "mcpatcher/cit/tower/heart.png")
        }
    }
    val protectionBox = carved {
        origin = BOTTOM
        align = BOTTOM
        offset = V3(68.0, -24.0)
        size = V3(45.0, 12.0)
        color = Color(0, 0, 0, 0.62)
        enabled = false
        +rectangle {
            origin = CENTER
            align = LEFT
            size = V3(10.0, 10.0, 10.0)
            color = WHITE
            offset.x += 8.0
            textureLocation = ResourceLocation.of("minecraft", "mcpatcher/cit/tower/protection.png")
        }
        +text {
            origin = CENTER
            align = CENTER
            scale = V3(0.95, 0.95, 0.95)
            shadow = true
            offset.x += 5.0
            content = "0%"
        }
    }

    init {
        UIEngine.overlayContext.addChild(protectionBox, tokenBox, healthBox)

        mod.registerChannel("tower:tokens") {
            val tokens = readDouble()
            (tokenBox.children[4] as TextElement).content = Formatter.toFormat(tokens)
        }
    }
}