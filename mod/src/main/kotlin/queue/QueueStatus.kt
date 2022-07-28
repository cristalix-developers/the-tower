package queue

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

/**
 * @project : tower
 * @author : Рейдж
 **/

private const val margin = 3
private const val width = 140.0

class QueueStatus {
    private var counter = 0
    private var total = 0
    private var need = 80

    private lateinit var icon: RectangleElement
    private lateinit var title: TextElement
    private lateinit var online: TextElement
    private lateinit var time: TextElement
    private lateinit var background: RectangleElement
    private lateinit var cancel: RectangleElement

    private val box = rectangle {
        scale = V3(1.5, 1.5, 1.0)
        enabled = false

        align = TOP
        origin = TOP
        offset.y += -width + 15

        size = V3(width, width / 4.0 * 1.2857142857, 1.0)

        icon = +rectangle {
            size = V3(width / 4.0, width / 4.0 * 1.2857142857)
            color = WHITE
            align = TOP_LEFT
            origin = TOP_LEFT

            time = +text {
                align = TOP
                origin = TOP
                color = WHITE
                shadow = true
                scale = V3(0.9, 0.9)
                offset.y += margin
            }
        }

        background = +rectangle {
            size = V3(width - width / 4.0, width / 4.0, 1.0)
            color = Color(0, 0, 0, 0.62)
            align = TOP_RIGHT
            origin = TOP_RIGHT
            title = +text {
                align = TOP_LEFT
                origin = TOP_LEFT
                offset.x += margin + 0.2
                offset.y += margin
                scale = V3(0.9, 0.9)
                content = "Загрузка..."
                color = WHITE
                shadow = true
            }
            online = +text {
                align = TOP_LEFT
                origin = TOP_LEFT
                offset.x += margin + 0.2
                offset.y += margin + 13
                scale = V3(0.9, 0.9)
                content = "§b0 из $need"
                color = WHITE
                shadow = true
            }
        }

        cancel = +rectangle {
            align = BOTTOM_RIGHT
            origin = BOTTOM_RIGHT
            size = V3(width / 4 * 3, width / 4.0 * 1.2857142857 - width / 4, 1.0)
            color = Color(255, 0, 0, 0.62)
            offset.y -= 1

            onClick {
                UIEngine.clientApi.clientConnection().sendPayload("queue:leave", Unpooled.EMPTY_BUFFER)
            }

            +text {
                align = LEFT
                origin = LEFT
                color = WHITE
                scale = V3(0.9, 0.9)
                offset.x += margin + 0.2
                content = "Покинуть очередь"
            }

            +text {
                align = RIGHT
                origin = RIGHT
                color = WHITE
                scale = V3(0.9, 0.9)
                offset.x -= margin
                content = ">"
            }
        }
    }

    init {
        UIEngine.overlayContext + box

        var before = System.currentTimeMillis()

        mod.registerHandler<GameLoop> {
            if (!box.enabled)
                return@registerHandler
            val now = System.currentTimeMillis()

            if (now - before > 1000) {
                before = now
                counter++
                time.content = "⏰ ${counter / 60}:${(counter % 60).toString().padStart(2, '0')}"
                online.content = "§7$total из $need"
            }
        }

        mod.registerChannel("queue:show") {
            if (!box.enabled) {
                box.animate(0.4, Easings.BACK_OUT) {
                    offset.y = 15.0
                }
            }

            before = System.currentTimeMillis()

            val address = NetUtil.readUtf8(this)
            need = readInt()

            icon.textureLocation = ResourceLocation.of("cache/animation", address)
            title.content = "§bРейтинговая игра"
            online.content = "§7$total из $need"

            box.enabled = true
        }

        mod.registerChannel("queue:online") {
            val currentTotal = readInt()
            total = currentTotal
            if (counter >= 300)
                UIEngine.clientApi.clientConnection().sendPayload("queue:leave", Unpooled.EMPTY_BUFFER)
        }

        mod.registerChannel("queue:hide") {
            box.animate(0.25, Easings.QUART_IN) {
                offset.y = -width + 15
            }
            UIEngine.schedule(0.26) {
                counter = 0
                box.enabled = false
            }
        }
    }
}