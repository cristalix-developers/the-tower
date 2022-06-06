package player

import dev.xdark.clientapi.event.lifecycle.GameLoop
import implario.humanize.Humanize
import mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

/**
 * @project tower
 * @author Рейдж
 */
object Statistic {

    val tokensBox = text {
        enabled = false
        offset = V3(-3.0, -24.0)
        origin = BOTTOM_RIGHT
        shadow = true
        content = ""
    }
    init {
        val moneyBox = text {
            offset = V3(-3.0, -14.0)
            origin = BOTTOM_RIGHT
            shadow = true
            content = ""
        }

        val box = rectangle {
            color = Color(0, 0, 0, 0.62)
            align = Relative.BOTTOM_RIGHT
            origin = BOTTOM_RIGHT
            addChild(moneyBox, tokensBox)
        }

        UIEngine.overlayContext + box

        repeat(2) { box.children.add(rectangle { color = TRANSPARENT }) }

        mod.registerChannel("tower:money") {
            val money = readInt()
            moneyBox.content = "${
                Humanize.plurals(
                    "Монета",
                    "Монеты",
                    "Монет",
                    money
                )
            } §e$money"
        }

        mod.registerChannel("tower:tokens") {
            val tokens = readInt()

            tokensBox.content = "${
                Humanize.plurals(
                    "Жетон",
                    "Жетона",
                    "Жетонов",
                    tokens
                )
            } §b$tokens"
        }
    }
}