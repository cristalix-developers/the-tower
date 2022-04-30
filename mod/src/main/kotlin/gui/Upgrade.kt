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
        val margin = 20.0
        origin = TOP
        align = TOP
        offset = V3(
            100 + if (index % 2 == 0) -205.0 else 5.0,
            20.0 + margin * index / 2 + size.y * index / 2 + if (index % 2 == 0) 0.0 else -margin / 2
        )
        size = V3(100.0, 70.0)
        color = Color(42,102,190,0.2)
    }
)
