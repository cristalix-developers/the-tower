package player

import dev.xdark.clientapi.event.lifecycle.GameLoop
import mod
import plural
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*
import screenCheck
import util.Formatter

/**
 * @project tower
 * @author Рейдж
 */
object Statistic {

    private val tokensBox = text {
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
            val money = readDouble()
            moneyBox.content = "${money.plural("Монета", "Монеты", "Монет")} §e${Formatter.toFormat(money)}"
        }

        mod.registerChannel("tower:tokens") {
            val tokens = readDouble()
            tokensBox.content = "§f${tokens.plural("Жетон", "Жетона", "Жетонов")} §b${Formatter.toFormat(tokens)}"
        }

        mod.registerHandler<GameLoop> {
            moneyBox.enabled = if (mod.gameActive) true else screenCheck()
            tokensBox.enabled = mod.gameActive
        }
    }
}