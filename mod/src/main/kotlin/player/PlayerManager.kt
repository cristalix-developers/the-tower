package player

import dev.xdark.clientapi.event.lifecycle.GameLoop
import mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import screenCheck
import util.Formatter

/**
 * @project tower
 * @author Рейдж
 */
class PlayerManager {

    private lateinit var progress: CarvedRectangle
    private lateinit var experienceContent: TextElement
    private val experiencePanel = carved {
        origin = BOTTOM
        align = BOTTOM
        size = V3(90.0, 13.0)
        color = Color(0, 0, 0, 0.62)
        offset = V3(-46.0, -24.0)
        progress = carved {
            origin = CENTER
            align = CENTER
            size = V3(0.0, 13.0, 0.0)
            color = Color(34, 184, 77, 1.0)
        }
        experienceContent = text {
            origin = CENTER
            align = CENTER
            shadow = true
            content = "Загрузка..."
        }
        +progress
        +experienceContent
    }

    private lateinit var levelContent: TextElement
    private val levelPanel = carved {
        origin = BOTTOM
        align = BOTTOM
        size = V3(90.0, 13.0)
        color = Color(0, 0, 0, 0.62)
        offset = V3(46.0, -24.0)
        levelContent = text {
            origin = CENTER
            align = CENTER
            shadow = true
            content = "Загрузка..."
        }
        +levelContent
    }

    companion object {
        var swordDamage = 1.0
    }

    init {
        UIEngine.overlayContext.addChild(experiencePanel, levelPanel)

        mod.registerChannel("tower:level") {
            val level = readUtf8()
            levelContent.content = level
        }

        mod.registerChannel("tower:exp") {
            val emoji = readUtf8()
            val experience = readDouble()
            val requiredExperience = readInt()

            progress.animate(1) { size.x = 58.0 / requiredExperience * experience }
            experienceContent.content = "$emoji ${Formatter.toFormat(experience)} из $requiredExperience"
        }

        mod.registerChannel("user:sword") {
            swordDamage = readDouble()
        }

        mod.registerHandler<GameLoop> {
            val has = !mod.gameActive && screenCheck()
            experiencePanel.enabled = has
            levelPanel.enabled = has
        }
    }
}