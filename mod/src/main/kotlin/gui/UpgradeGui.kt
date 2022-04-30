package gui

import dev.xdark.feder.NetUtil
import mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

object UpgradeGui {

    private val minecraft = UIEngine.clientApi.minecraft()
    private val gui = ContextGui()

    val upgrade = rectangle {
        size = V3(minecraft.displayWidth.toDouble(), minecraft.displayHeight.toDouble())
        align = CENTER
        origin = CENTER
        color = Color(0,0,0,0.86)
    }

    init {
        gui + upgrade

        mod.registerChannel("upgradegui:init") {
            if (upgrade.children.size <= readInt())
                upgrade + Upgrade(NetUtil.readUtf8(this), readInt(), readInt(), NetUtil.readUtf8(this)).element
            gui.open()
        }
    }
}