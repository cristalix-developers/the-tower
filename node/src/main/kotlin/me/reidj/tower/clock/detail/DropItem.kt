package me.reidj.tower.clock.detail

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import me.reidj.tower.app
import me.reidj.tower.tournament.TournamentManager
import org.bukkit.Material

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class DropItem {

    private val gem = item {
        type = Material.CLAY_BALL
        nbt("tower", "gem")
    }
    private val locations = app.worldMeta.getLabels("gem")

    init {
        B.repeat(20 * 120) {
            TournamentManager.getOnlinePlayers()
                .forEach { _ -> app.worldMeta.world.dropItem(locations.random(), gem) }
        }
    }
}