package gui

import dev.xdark.feder.NetUtil
import mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.TOP
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

object UpgradeGui {

    private val minecraft = UIEngine.clientApi.minecraft()
    private val gui = ContextGui()

    val upgrade = rectangle {
        size = V3(minecraft.displayWidth.toDouble(), minecraft.displayHeight.toDouble())
        align = TOP
        origin = TOP
        color = Color(0,0,0,0.86)
    }

    init {
        gui + upgrade

        mod.registerChannel("upgradegui:init") {
            val index = readInt()
            val title = NetUtil.readUtf8(this)
            val cost = readInt()
            val level = readInt()
            val lore = NetUtil.readUtf8(this)
            println(index)
            if (upgrade.children.size <= index)
                upgrade + Upgrade(title, cost, level, lore).element
            gui.open()
        }
    }
}