package me.reidj.tower.arena

import me.reidj.tower.app

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class ArenaManager {

    companion object {
        lateinit var arenas: List<Arena>
    }

    init {
        arenas = app.worldMeta.getLabels("arena").map { label ->
            val data = label.tag.split(" ")
            val arenaNumber = data[0].toInt()
            Arena(
                arenaNumber,
                app.worldMeta.getLabel("$arenaNumber-start").apply {
                    yaw = tagFloat
                    x += 0.5
                    z += 0.5
                },
                app.worldMeta.getLabel("$arenaNumber-tower").apply {
                    x += 0.5
                    z += 0.5
                },
                app.worldMeta.getLabels("$arenaNumber-mob").filter { it.distanceSquared(label) < 900 }.toSet(),
                app.worldMeta.getLabels("$arenaNumber-gem").toSet()
            )
        }.sortedBy { it.arenaNumber }
    }
}