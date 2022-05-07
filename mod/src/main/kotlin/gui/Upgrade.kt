package gui

import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.TOP
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

data class Upgrade(
    val title: String,
    val cost: Int,
    val level: Int,
    val lore: String,
    val element: RectangleElement = rectangle {
        val index = UpgradeGui.upgrade.children.size
        val margin = 75.0
        origin = TOP
        align = TOP
        offset = V3(
            -(2 - index) * 105.0,
            150.0 + (2 - index) / 2
            //150.0 + margin * index / 2
        )
        size = V3(100.0, 70.0)
        color = Color(42,102,190,0.2)
    }
)
